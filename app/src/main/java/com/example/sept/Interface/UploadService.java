package com.example.sept.Interface;


import com.example.sept.Class.ConversationAcceptList;
import com.example.sept.Class.ConversationList;
import com.example.sept.Class.CustomContent;
import com.example.sept.Class.IdealContent;
import com.example.sept.Class.MyWannabeSave;
import com.example.sept.Class.PersonalPicGet;
import com.example.sept.Class.PersonalProfileRe;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadService {

    @Multipart
    @POST("/personal_pic_upload.php")
    Call<ResponseBody> uploadFile(
                    @Part("description") RequestBody description,
                    @Part MultipartBody.Part file);

    @Multipart
    @POST("/chat_pic_upload.php")
    Call<ResponseBody> uploadChat(
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file);

    @Multipart
    @POST("/personal_pic_get.php")
    Call<PersonalPicGet> profilePic(
            @Part("description") RequestBody description);

//    마이페이지 -> 내용 보여주기 위해 + 이미지 수정도 같이
    @Multipart
    @POST("/PersonalProfileRe.php")
    Call<PersonalProfileRe> profileRe(
            @Part("description") RequestBody description);

    //    마이페이지를 통해 프로필 수정할 때 -> text
    @Multipart
    @POST("/PersonalProfileReText.php")
    Call<PersonalProfileRe> profileReText(
            @Part("description") RequestBody description,
            @Part("description1") RequestBody description1,
            @Part("description2") RequestBody description2,
            @Part("description3") RequestBody description3,
            @Part("description4") RequestBody description4,
            @Part("description5") RequestBody description5);

    //    전체 회원목록 가지고 올때

    @Multipart
    @POST("/CustomList.php")
    Call<CustomContent> customList(
            @Part("description") RequestBody description);

    //    이상형 검색조건 등록할 때, -> 이미지 전송을 생각했었음...
    @Multipart
    @POST("/my_wannabe_save.php")
    Call<ResponseBody> wannabeSave(
            @Part("c_id_email") RequestBody c_id_email,
            @Part("w_gender") RequestBody w_gender,
            @Part("w_region") RequestBody w_region,
            @Part("w_job") RequestBody w_job,
            @Part("w_age_min") RequestBody w_age_min,
            @Part("w_age_max") RequestBody w_age_max,
            @Part("w_smoke") RequestBody w_smoke,
            @Part("w_height_min") RequestBody w_height_min,
            @Part("w_height_max") RequestBody w_height_max,
            @Part MultipartBody.Part file);

    //    이상형 검색조건 등록할 때,
    @Multipart
    @POST("/my_wannabe_save.php")
    Call<ResponseBody> wannabeSave2(
            @Part("c_id_email") RequestBody c_id_email,
            @Part("w_gender") RequestBody w_gender,
            @Part("w_region") RequestBody w_region,
            @Part("w_job") RequestBody w_job,
            @Part("w_age_min") RequestBody w_age_min,
            @Part("w_age_max") RequestBody w_age_max,
            @Part("w_smoke") RequestBody w_smoke,
            @Part("w_height_min") RequestBody w_height_min,
            @Part("w_height_max") RequestBody w_height_max);


    // 이상형 검색한 값,
    @Multipart
    @POST("/my_wannabe_choice.php")
    Call<CustomContent> wannabeChoice(
            @Part("c_id_email") RequestBody c_id_email);


    // 이상형 검색 설정 여부,
    @Multipart
    @POST("/my_wannabe_save_check.php")
    Call<JsonObject> wannabeSaveCheck(
            @Part("c_id_email") RequestBody c_id_email);


    // 이상형 검색한 기록 여부,
    @Multipart
    @POST("/my_wannabe_save_before.php")
    Call<MyWannabeSave> wannabeSaveBefore(
            @Part("c_id_email") RequestBody c_id_email);


    // 프로필 선택했을 때 -> 상세하게 보여주는 화면
    @Multipart
    @POST("/profile_show.php")
    Call<JsonObject> profileShow(
            @Part("c_id_email") RequestBody c_id_email);


    // 대화 신청하는거
    @Multipart
    @POST("/converse_ask.php")
    Call<JsonObject> converseAsk(
            @Part("from_id") RequestBody from_id,
            @Part("to_id") RequestBody to_id);


    // 대화 신청왔을때 notification -> service단에서
    @Multipart
    @POST("/notification_service.php")
    Call<CustomContent> notificationService(
            @Part("my_id") RequestBody my_id);


    // 나의 대화신청 항목들 보여주기
    @Multipart
    @POST("/conversComeList.php")
    Call<CustomContent> conversComeList(
            @Part("my_id") RequestBody my_id);

    // 대화 신청 수락 or 거절 or 취소
    @Multipart
    @POST("/converse_ask_answer.php")
    Call<JsonObject> converseAskAnswer(
            @Part("my_id") RequestBody my_id,
            @Part("from_id") RequestBody from_id,
            @Part("to_id") RequestBody to_id,
            @Part("answer") RequestBody answer);

    // 대화방 만들기
    @Multipart
    @POST("/conversation_room.php")
    Call<JsonObject> conversationRoom(
            @Part("from_id") RequestBody from_id,
            @Part("to_id") RequestBody to_id
    );

    // 대화방->check -> 만들기
    @Multipart
    @POST("/conversation_room_list.php")
    Call<ConversationList> conversationRoomList(
            @Part("my_id") RequestBody my_id
    );

    // 대화방->check-> 만들기
    @Multipart
    @POST("/conversation_room_list2.php")
    Call<ConversationAcceptList> conversationRoomList2(
            @Part("my_id") RequestBody my_id
    );


    //이상형 선택 1,2지망 보기
    @Multipart
    @POST("/IdealList.php")
    Call<IdealContent> idealList(
            @Part("id") RequestBody id,
            @Part("gender") RequestBody gender);


    //이상형 선택 1,2지망 보기
    @Multipart
    @POST("/idealPic.php")
    Call<JsonObject> idealPicList(
            @Part("w_pic_id") RequestBody w_pic_id,
            @Part("w_pic_1") RequestBody w_pic_1,
            @Part("w_pic_2") RequestBody w_pic_2);


    //얼굴매칭도 저장하기
    @Multipart
    @POST("/matching")
    Call<ResponseBody> matchingFace(
            @Part("id") RequestBody id,
            @Part("path") RequestBody path);

    //얼굴매칭도 저장하기->change
    @Multipart
    @POST("/matchingChange")
    Call<ResponseBody> matchingChangeFace(
            @Part("id") RequestBody id,
            @Part("path") RequestBody path);


    //이상형 회원목록 가져올때
    @Multipart
    @POST("/idealContentsList.php")
    Call<CustomContent> idealContentsList(
            @Part("id") RequestBody id);

    //이상형 회원목록 가져올때
    @Multipart
    @POST("/idealContestList2.php")
    Call<CustomContent> idealContentsList2(
            @Part("id") RequestBody id);

    //1번째 이상형
    @Multipart
    @POST("/my_wannabe_choice_ideal.php")
    Call<CustomContent> idealWannabeChoice(
            @Part("c_id_email") RequestBody c_id_email);

    //2번째 이상형
    @Multipart
    @POST("/my_wannabe_choice_ideal2.php")
    Call<CustomContent> idealWannabeChoice2(
            @Part("c_id_email") RequestBody c_id_email);
}
