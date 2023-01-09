package online.daliyq.ui.profile

import androidx.paging.PagingSource
import androidx.paging.PagingState
import online.daliyq.api.ApiService
import online.daliyq.api.response.QuestionAndAnswer
import java.time.LocalDate

class UserAnswerPagingSource(val api: ApiService, val uid: String) : PagingSource<LocalDate, QuestionAndAnswer>(){
    override fun getRefreshKey(state: PagingState<LocalDate, QuestionAndAnswer>): LocalDate? = null

    override suspend fun load(params: LoadParams<LocalDate>): LoadResult<LocalDate, QuestionAndAnswer> {
        val userAnswersResponse = api.getUserAnswers(uid, params.key)

        return if(userAnswersResponse.isSuccessful){
            val userAnswers = userAnswersResponse.body()!!
            val nextkey = if(userAnswers.isNotEmpty()){
                userAnswers.minOf { it.question.id }
            } else {
                null
            }

            LoadResult.Page(
                data = userAnswers,
                prevKey = null,
                nextKey = nextkey
            )
        } else {
            LoadResult.Error(Throwable("Paging Error"))
        }
    }
}