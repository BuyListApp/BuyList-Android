package ru.buylist.presentation.pattern_detail

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.buylist.R
import ru.buylist.data.Result
import ru.buylist.data.entity.GlobalItem
import ru.buylist.data.entity.Item
import ru.buylist.data.repositories.pattern.PatternsDataSource
import ru.buylist.data.wrappers.CircleWrapper
import ru.buylist.data.wrappers.ItemWrapper
import ru.buylist.presentation.data.SnackbarData
import ru.buylist.presentation.word_tips_adapter.WordTipsAdapter
import ru.buylist.utils.CategoryInfo
import ru.buylist.utils.Event
import ru.buylist.utils.JsonUtils

class PatternDetailViewModel(private val repository: PatternsDataSource) : ViewModel() {

    private val items = mutableListOf<Item>()
    private val wordTips = mutableListOf<GlobalItem>()

    // Fields for two-way databinding
    val productName = MutableLiveData<String>()
    val productQuantity = MutableLiveData<String>()
    val productUnit = MutableLiveData<String>()

    // Flags to show and hide buttons
    val fabIsShown = MutableLiveData<Boolean>(true)
    val prevArrowIsShown = MutableLiveData<Boolean>(true)
    val nextArrowIsShown = MutableLiveData<Boolean>(true)

    // Triggers to load and update products in the database
    private val _patternId = MutableLiveData<Long>()
    private val _productToEdit = MutableLiveData<Int>()

    private val _triggers = MediatorLiveData<Pair<Long?, Int?>>()
            .apply {
                addSource(_patternId) { value = Pair(it, _productToEdit.value) }
                addSource(_productToEdit) { value = Pair(_patternId.value, it) }
            }

    private val _products = _triggers.switchMap { triggers ->
        repository.observePattern(triggers.first).map { computeResult(it, triggers.second) }
    }
    val products: LiveData<List<ItemWrapper>?> = _products

    val listIsEmpty: LiveData<Boolean> = Transformations.map(_products) {
        it == null || it.isEmpty()
    }

    // Word tips
    private val _wordTipsToShow: LiveData<List<GlobalItem>> = productName.map { query ->
        if (query == null || query.isEmpty()) {
            emptyList()
        } else {
            filterTags(query)
        }
    }

    val wordTipsToShow: LiveData<List<GlobalItem>> = _wordTipsToShow

    // Triggers to load and update selected color
    private val _colors = MutableLiveData<List<String>>()
    private val _selectedColor = MutableLiveData<Int>()

    private val _circlesUpdate
            = MediatorLiveData<Pair<List<String>?, Int?>>().apply {
        addSource(_colors) { value = Pair(it, _selectedColor.value) }
        addSource(_selectedColor) { value = Pair(_colors.value, it) }
    }

    private val _circles: LiveData<List<CircleWrapper>> = _circlesUpdate.map { pair ->
        getWrappedCircles(pair.first, pair.second)
    }
    val circles: LiveData<List<CircleWrapper>> = _circles

    // Events
    private val _snackbarText = MutableLiveData<Event<SnackbarData>>()
    val snackbarText: LiveData<Event<SnackbarData>> = _snackbarText

    private val _addProductEvent = MutableLiveData<Event<Unit>>()
    val addProductEvent: LiveData<Event<Unit>> = _addProductEvent

    private val _saveProductEvent = MediatorLiveData<Event<Unit>>()
    val saveProductEvent: LiveData<Event<Unit>> = _saveProductEvent

    private val _productsAddedEvent = MutableLiveData<Event<Unit>>()
    val productsAddedEvent: LiveData<Event<Unit>> = _productsAddedEvent


    fun start(patternId: Long, newColors: List<String>) {
        _patternId.value = patternId
        _colors.value = newColors
        loadWordTips()
    }

    fun addProduct() {
        _addProductEvent.value = Event(Unit)
    }

    fun hideNewProductLayout() {
        _productsAddedEvent.value = Event(Unit)
        _selectedColor.value = null
    }

    fun setProductInfoByWordTip(wordTip: GlobalItem) {
        productName.value = wordTip.name

        circles.value?.let {
            for (circle in it) {
                if (circle.color == wordTip.color) {
                    _selectedColor.value = circle.position
                    break
                }
            }
        }
    }

    fun saveNewItem() {
        val name = productName.value
        if (name == null) {
//            showSnackbarMessage(R.string.snackbar_empty_product_name)
            return
        }

        val newProduct = Item(name = name, color = getColor())

        val quantity = productQuantity.value
        if (quantity != null) {
            newProduct.quantity = quantity
        }

        val unit = productUnit.value
        if (unit != null && quantity != null) {
            newProduct.unit = unit
        }

        createProduct(newProduct)
        productName.value = null
        productQuantity.value = null
        productUnit.value = null
        _saveProductEvent.value = Event(Unit)
    }

    fun edit(wrapper: ItemWrapper) {
        _productToEdit.value = wrapper.position
    }

    fun saveEditedData(wrapper: ItemWrapper, newName: String, newQuantity: String, newUnit: String) {
        if (newName.isEmpty()) {
//            showSnackbarMessage(R.string.snackbar_empty_product_name)
            return
        }

        viewModelScope.launch {
            items[wrapper.position].apply {
                name = newName
                quantity = newQuantity
                unit = newUnit
            }
            repository.updateProducts(_patternId.value, JsonUtils.convertItemsToJson(items))
        }
        _productToEdit.value = null
        fabIsShown.value = true
    }

    fun delete(wrapper: ItemWrapper) = viewModelScope.launch {
        items.remove(wrapper.item)
        repository.updateProducts(_patternId.value, JsonUtils.convertItemsToJson(items))
    }

    fun updateCircle(selectedCircle: CircleWrapper) {
        _selectedColor.value = selectedCircle.position
    }

    fun showHideArrows(prev: Boolean, next: Boolean) {
        prevArrowIsShown.value = prev
        nextArrowIsShown.value = next
    }

    fun showHideFab(dy: Int) {
        if (_productToEdit.value != null) {
            fabIsShown.value = false
            return
        }
        fabIsShown.value = (dy <= 0)
    }

    private fun createProduct(item: Item) = viewModelScope.launch {
        items.add(item)
        items.sortWith(compareBy({ it.isPurchased }, { it.color }, { it.id }))
        repository.updateProducts(_patternId.value, JsonUtils.convertItemsToJson(items))
    }

    private fun getColor(): String {
        _selectedColor.value?.let {position ->
            _colors.value?.let { colors ->
                return colors[position]
            }
        } ?: return CategoryInfo.COLOR
    }

    private fun getWrappedCircles(list: List<String>?, position: Int?): List<CircleWrapper> {
        if (list == null) return emptyList()

        val newList = mutableListOf<CircleWrapper>()
        for ((i, circle) in list.withIndex()) {
            val circleWrapper = CircleWrapper(circle, i)
            if (position != null && position == i) {
                circleWrapper.isSelected = true
            }
            newList.add(circleWrapper)
        }
        return newList
    }

    private fun getWrappedItems(list: List<Item>, position: Int?) : List<ItemWrapper> {
        val newList = mutableListOf<ItemWrapper>()
        for ((i, item) in list.withIndex()) {
            val wrappedItem = ItemWrapper(item.copy(), i)
            if (position != null && position == i) {
                wrappedItem.isEditable = true
            }
            newList.add(wrappedItem)
        }
        return newList
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(SnackbarData(message))
    }

    private fun filterTags(query: CharSequence): List<GlobalItem> {
        val newTags = mutableListOf<GlobalItem>()
        var count = 0
        for (tag in wordTips) {
            if (count == WordTipsAdapter.MAX_WORD_TIPS) break
            if (tag.name.startsWith(query, true)) {
                newTags.add(tag)
                count++
            }
        }
        return newTags
    }

    private fun loadWordTips() {
        viewModelScope.launch {
            val result = repository.getTags()

            if (result is Result.Success) {
                wordTips.clear()
                wordTips.addAll(result.data)
            }
        }
    }

    private fun computeResult(productsResult: Result<String>, position: Int?): List<ItemWrapper>? {
        return if (productsResult is Result.Success) {
            items.clear()
            items.addAll(JsonUtils.convertItemsFromJson(productsResult.data))
            getWrappedItems(items, position)
        } else {
            showSnackbarMessage(R.string.snackbar_products_loading_error)
            null
        }
    }


}