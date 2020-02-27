package com.aasoo.freshifybeta.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA06wI3T0:APA91bHv2qJ1FuoS8SvysBVVsLMdSwxBlD7R15fo8U0eSHk6ucO8W8TkdQieSVaWzBEdxMFDH4okuts7gWmOVezB1oRX-NmDtAGssp_8RL71g5uiw7PKcI00_uFmFUD7D9fnKLp0T-37"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
