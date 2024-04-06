package com.thomaskioko.tvmaniac.core.paging

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.CombinedLoadStates
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class SwiftUiPagingHelper<T : Any> {

  private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
  private val workerDispatcher: CoroutineDispatcher = Dispatchers.Default

  private val diffCallback =
    object : DiffUtil.ItemCallback<T>() {
      override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
      }

      override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
      }
    }

  private val differ =
    AsyncPagingDataDiffer(
      diffCallback,
      object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}

        override fun onRemoved(position: Int, count: Int) {}

        override fun onMoved(fromPosition: Int, toPosition: Int) {}

        override fun onChanged(position: Int, count: Int, payload: Any?) {}
      },
      mainDispatcher = mainDispatcher,
      workerDispatcher = workerDispatcher,
    )

  suspend fun submitData(pagingData: PagingData<T>) {
    differ.submitData(pagingData)
  }

  fun retry() {
    differ.retry()
  }

  fun refresh() {
    differ.refresh()
  }

  fun loadNextPage() {
    val index = getItemCount() - 1
    differ.getItem(index)
  }

  private fun getItemCount(): Int {
    return differ.itemCount
  }

  fun getItems() = differ.snapshot().items

  val loadStateFlow: Flow<CombinedLoadStates> = differ.loadStateFlow

  val onPagesUpdatedFlow: Flow<Unit> = differ.onPagesUpdatedFlow
}
