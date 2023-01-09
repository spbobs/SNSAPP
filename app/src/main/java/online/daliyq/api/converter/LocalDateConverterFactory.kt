package online.daliyq.api.converter

import retrofit2.Converter
import retrofit2.Converter.Factory
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.time.LocalDate

class LocalDateConverterFactory : Factory() {
    override fun stringConverter(
        type: Type, annotations: Array<out Annotation>, retrofit: Retrofit
    ): Converter<LocalDate, String>? {
        if (type == LocalDate::class.java){
            return Converter<LocalDate, String> { it.toString() }
        }
        return null
    }
}