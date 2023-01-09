package online.daliyq.ui.timeline

import androidx.paging.PagingSource
import androidx.paging.PagingState
import online.daliyq.api.ApiService
import online.daliyq.api.response.Question
import java.time.LocalDate

class TimelinePagingSource(val api: ApiService) : PagingSource<LocalDate, Question>(){
    override fun getRefreshKey(state: PagingState<LocalDate, Question>): LocalDate? = null

    override suspend fun load(params: LoadParams<LocalDate>): LoadResult<LocalDate, Question> {
        val fromDate = params.key ?: LocalDate.now()

        val questionsResponse = api.getQuestions(fromDate, params.loadSize)

        if(questionsResponse.isSuccessful){
            val questions = questionsResponse.body()!!

            if(questions.isNotEmpty()){
                val oldest = questions.minOf { it.id }
                val nextKey = oldest.minusDays(1)

                return LoadResult.Page(
                    data = questions,
                    prevKey = null,
                    nextKey = nextKey
                )
            }

            return LoadResult.Page(
                data = questions,
                prevKey = null,
                nextKey = null
            )
        }
        return LoadResult.Error(Throwable("Paging Error"))
    }
}