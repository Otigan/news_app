package com.example.newsapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsapp.data.remote.api.NewsAPI
import com.example.newsapp.data.remote.model.Article
import retrofit2.HttpException
import java.io.IOException

class NewsPagingSource(
    private val newsAPI: NewsAPI
) : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val position = params.key ?: 1
        return try {

            val response = newsAPI.topHeadlines(
                "us",
                position,
                30
            )
            LoadResult.Page(
                response.articles,
                prevKey = if (position == 1) null else position.minus(1),
                nextKey = if (response.articles.isEmpty()) null else position.plus(1)
            )
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: IOException) {
            LoadResult.Error(e)
        }
    }
}