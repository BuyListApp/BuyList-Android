package ru.buylist.presentation.product_dictionary

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.buylist.data.Result
import ru.buylist.data.Result.Success
import ru.buylist.data.entity.GlobalItem
import ru.buylist.data.repositories.items.GlobalItemsDataSource
import ru.buylist.data.wrappers.CircleWrapper
import ru.buylist.data.wrappers.GlobalItemWrapper
import ru.buylist.presentation.data.SnackbarData
import ru.buylist.utils.CategoryInfo
import ru.buylist.utils.Event

class ProductDictionaryViewModel(
    private val repository: GlobalItemsDataSource
) : ViewModel() {

    private val _forceUpdate = MutableLiveData(false)
    private val _productToEdit = MutableLiveData<Int>(null)
    private val _searchQuery = MutableLiveData<String>(null)

    private val _triggers = MediatorLiveData<ProductDictionaryUpdatedData>()
        .apply {
            addSource(_forceUpdate) { forceUpdate ->
                value = ProductDictionaryUpdatedData(
                    forceUpdate = forceUpdate,
                    productPositionToEdit = _productToEdit.value,
                    searchQuery = _searchQuery.value
                )
            }
            addSource(_productToEdit) { productPositionToEdit ->
                value = ProductDictionaryUpdatedData(
                    forceUpdate = _forceUpdate.value ?: false,
                    productPositionToEdit = productPositionToEdit,
                    searchQuery = _searchQuery.value
                )
            }
            addSource(_searchQuery) { searchQuery ->
                value = ProductDictionaryUpdatedData(
                    forceUpdate = _forceUpdate.value ?: false,
                    productPositionToEdit = _productToEdit.value,
                    searchQuery = searchQuery
                )
            }
        }

    private val _products: LiveData<List<GlobalItemWrapper>> = _triggers
        .switchMap { (_, editablePosition, searchQuery) ->
            repository
                .observeGlobalItems()
                .map { productsResult ->
                    loadProducts(
                        productsResult = productsResult,
                        editablePosition = editablePosition,
                        searchQuery = searchQuery
                    )
                }
        }

    val products: LiveData<List<GlobalItemWrapper>> = _products

    private val _colors = MutableLiveData<List<String>>()
    private val _selectedColor = MutableLiveData<Int>()

    private val _circlesUpdate = MediatorLiveData<Pair<List<String>?, Int?>>().apply {
        addSource(_colors) { value = Pair(it, _selectedColor.value) }
        addSource(_selectedColor) { value = Pair(_colors.value, it) }
    }

    private val _circles: LiveData<List<CircleWrapper>> = _circlesUpdate.map { pair ->
        getWrappedCircles(pair.first, pair.second)
    }
    val circles: LiveData<List<CircleWrapper>> = _circles

    val listIsEmpty: LiveData<Boolean> = Transformations.map(_products) {
        it == null || it.isEmpty()
    }

    val productName = MutableLiveData<String>()

    val fabIsShown = MutableLiveData(true)
    val prevArrowIsShown = MutableLiveData(true)
    val nextArrowIsShown = MutableLiveData(true)

    private val _snackbarText = MutableLiveData<Event<SnackbarData>>()
    val snackbarText: LiveData<Event<SnackbarData>> = _snackbarText

    private val _addProductEvent = MutableLiveData<Event<Unit>>()
    val addProductEvent: LiveData<Event<Unit>> = _addProductEvent

    private val _productCreated = MutableLiveData<Event<Unit>>()
    val productCreated: LiveData<Event<Unit>> = _productCreated


    fun start(colors: List<String>) {
        _colors.value = colors
    }

    fun addProduct() {
        _addProductEvent.value = Event(Unit)
    }

    fun edit(wrapper: GlobalItemWrapper) {
        _productToEdit.value = wrapper.position
    }

    fun saveEditedData(wrapper: GlobalItemWrapper, newName: String) {
        wrapper.item.name = newName
        viewModelScope.launch {
            repository.updateGlobalItem(wrapper.item)
        }
        _productToEdit.value = null
        fabIsShown.value = true
    }

    fun saveNewProduct() {
        val name = productName.value
        if (name == null) {
//            showSnackbarMessage(R.string.snackbar_empty_product_name)
            return
        }

        createProduct(GlobalItem(name = name, color = getColor()))
        productName.value = null
    }

    fun delete(globalItemWrapper: GlobalItemWrapper) = viewModelScope.launch {
        repository.deleteGlobalItem(globalItemWrapper.item)
    }

    fun search(productName: String?) {
        _searchQuery.value = productName
    }

    fun hideNewProductLayout() {
        _productCreated.value = Event(Unit)
    }

    fun showHideFab(dy: Int) {
        fabIsShown.value = (dy <= 0)
    }

    fun showHideArrows(prev: Boolean, next: Boolean) {
        prevArrowIsShown.value = prev
        nextArrowIsShown.value = next
    }

    fun updateCircle(selectedCircle: CircleWrapper) {
        _selectedColor.value = selectedCircle.position
    }

    private fun createProduct(product: GlobalItem) = viewModelScope.launch {
        repository.saveGlobalItem(product)
    }

    private fun getColor(): String {
        _selectedColor.value?.let { position ->
            _colors.value?.let { colors ->
                return colors[position]
            }
        } ?: return CategoryInfo.COLOR
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(SnackbarData(message))
    }

    private fun getWrappedCircles(colors: List<String>?, position: Int?): List<CircleWrapper> {
        if (colors == null) return emptyList()

        val newList = mutableListOf<CircleWrapper>()
        for ((i, circle) in colors.withIndex()) {
            val circleWrapper = CircleWrapper(circle, i)
            if (position != null && position == i) {
                circleWrapper.isSelected = true
            }
            newList.add(circleWrapper)
        }
        return newList
    }

    private fun mapProducts(
        products: List<GlobalItem>,
        editablePosition: Int?
    ): List<GlobalItemWrapper> {
        return products
            .mapIndexed { index, product ->
                GlobalItemWrapper(
                    item = product,
                    position = index,
                    isEditable = editablePosition == index
                )
            }
            .sortedBy { it.item.name }
    }

    private fun loadProducts(
        productsResult: Result<List<GlobalItem>>,
        editablePosition: Int?,
        searchQuery: String?
    ): List<GlobalItemWrapper> {
        return if (productsResult is Success) {
            val items = mapProducts(
                products = productsResult.data,
                editablePosition = editablePosition
            )
            if (searchQuery == null) {
                items
            } else {
                items.filter { it.item.name.startsWith(searchQuery, true) }
            }
        } else {
            emptyList()
        }
    }

    private data class ProductDictionaryUpdatedData(
        val forceUpdate: Boolean,
        val productPositionToEdit: Int?,
        val searchQuery: String?
    )

}