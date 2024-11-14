package app.mirabellsoft.mirabell_library.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

class Registration {
    enum class ValidationResult {
        SUCCESS,                        // validation 성공
        ERROR_FAIL,                     // validation 실패
        ERROR_EMPTY,                    // 입력 문자가 없음
        ERROR_LENGTH,                   // 길이가 다름
        ERROR_INCLUDE_NUMBER,           // 숫자가 아님
        ERROR_INCLUDE_CHAR,             // 숫자가 아님
        ERROR_DATE_IS_FUTURE,           // 미래 날짜
        ERROR_EXCEPTION                 // Exception 발생
    }

    companion object {
        private const val TAG = "MirabellLib"

        private const val MOBILE_LENGTH = 11

        fun validationId(id: String) : ValidationResult {
            return try {
                when {
                    id.isEmpty() -> ValidationResult.ERROR_EMPTY
                    !Pattern.matches("^[A-Za-z0-9]*$", id) -> ValidationResult.ERROR_FAIL
                    else -> ValidationResult.SUCCESS
                }
            } catch (ex: Exception) {
                Log.d(TAG, "changeDisplayMobile Exception : ${ex.localizedMessage}")
                ValidationResult.ERROR_EXCEPTION
            }
        }

        // Mobile 번호 유효성 검사 api
        // param:
        //      mobile: String - 모바일 번호(only Number)
        // return:
        //      Registration.ValidationResult
        fun validationMobile(mobile: String) : ValidationResult {
            return try {
                when {
                    (mobile.length != MOBILE_LENGTH) -> ValidationResult.ERROR_LENGTH
                    !Pattern.matches("^010[2-9][0-9]{7}$", mobile) -> ValidationResult.ERROR_FAIL
                    else -> ValidationResult.SUCCESS
                }
            } catch (ex: Exception) {
                Log.d(TAG, "changeDisplayMobile Exception : ${ex.localizedMessage}")
                ValidationResult.ERROR_EXCEPTION
            }
        }

        // Date 유효성 검사 api
        // param:
        //      date: String        - 날짜(only Number)
        //      isBirthday: Boolean - 오늘 날짜 이후 유효성 확인
        // return:
        //      Registration.ValidationResult
        fun validationDate(date: String, isNotAllowFuture: Boolean): ValidationResult {
            if (date.trim().isEmpty()) {
                return ValidationResult.ERROR_EMPTY
            }

            if (date.trim().isNotEmpty() && date.trim().length < 8) {
                return ValidationResult.ERROR_LENGTH
            }

            val df = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            df.isLenient = false

            return try {
                if (isNotAllowFuture) {
                    val birthDate = df.parse(date)

                    val todayDate = Date()

                    if (birthDate?.before(todayDate) == true) ValidationResult.SUCCESS
                    else ValidationResult.ERROR_DATE_IS_FUTURE
                } else {
                    ValidationResult.SUCCESS
                }
            } catch (ex: Exception) {
                println("error")
                ValidationResult.ERROR_EXCEPTION
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        // Mobile 번호 Display 변환(- 추가) api
        // param:
        //      mobile: String - 모바일 번호(only Number)
        // return:
        //      성공 : "-"가 추가된 모바일 번호
        //      실패 : 전달 받은 mobile 번호 그대로 return
        fun displayMobile(mobile: String) : String {
            return try {
                when {
                    validationMobile(mobile) == ValidationResult.SUCCESS -> String.format(
                        "%3s-%4s-%4s",
                        mobile.substring(0, 3),
                        mobile.substring(3, 7),
                        mobile.substring(7, 11)
                    )

                    else -> mobile
                }
            }catch (ex: Exception) {
                Log.d(TAG, "changeDisplayMobile Exception : ${ex.localizedMessage}")
                mobile
            }
        }
    }
}