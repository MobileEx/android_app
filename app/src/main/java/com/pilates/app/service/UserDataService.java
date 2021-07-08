package com.pilates.app.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.pilates.app.R;
import com.pilates.app.model.HttpRequest;
import com.pilates.app.model.UserRole;
import com.pilates.app.model.UserSession;
import com.pilates.app.model.dto.AboutBioDto;
import com.pilates.app.model.dto.AddClassSelectionsDto;
import com.pilates.app.model.dto.BasicProfileDto;
import com.pilates.app.model.dto.ClassDetailsDto;
import com.pilates.app.model.dto.ClassFilterRequest;
import com.pilates.app.model.dto.ClassFilteredListDto;
import com.pilates.app.model.dto.ClassFullInfoDto;
import com.pilates.app.model.dto.ClassSpecDto;
import com.pilates.app.model.dto.ClassTimingDto;
import com.pilates.app.model.dto.DashboardInfoDto;
import com.pilates.app.model.dto.LoginResponseDto;
import com.pilates.app.model.dto.SaveGalleryResponseDto;
import com.pilates.app.model.dto.SelectionListDto;
import com.pilates.app.model.dto.StatusMessageDto;
import com.pilates.app.model.dto.TraineeSignupInfoDto;
import com.pilates.app.model.dto.TrainerCertificateListDto;
import com.pilates.app.model.dto.TrainerGalleryDto;
import com.pilates.app.model.dto.UserDetailListDto;
import com.pilates.app.model.dto.UserDto;
import com.pilates.app.model.dto.UserInfoDto;
import com.pilates.app.service.listener.OnRequestResult;
import com.pilates.app.util.Constant;
import com.pilates.app.util.DefaultOperations;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class UserDataService {
    private final Gson jsonConverter = new Gson();
    private final RequestQueue requestQueue;
    private Context context;

    public UserDataService(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void register(UserDto registerInfo, OnRequestResult<LoginResponseDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.POST,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/user/"
                        + (registerInfo.getRole() == UserRole.TRAINER ? "check-data" : "register"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final LoginResponseDto infoDto = jsonConverter.fromJson(infoString, LoginResponseDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, registerInfo);

        requestQueue.add(httpRequest);
    }

    public void saveBasicProfile(BasicProfileDto trainerBasicProfile, OnRequestResult<LoginResponseDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.POST,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/classinfo/save/basicprofile",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final LoginResponseDto infoDto = jsonConverter.fromJson(infoString, LoginResponseDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, trainerBasicProfile, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void login(UserDto dto, OnRequestResult<LoginResponseDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.POST,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/user/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final LoginResponseDto infoDto = jsonConverter.fromJson(infoString, LoginResponseDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, dto);

        requestQueue.add(httpRequest);
    }

    public void removeProfilePic(OnRequestResult<StatusMessageDto> listener) {

        HttpRequest httpRequest = new HttpRequest(Request.Method.POST,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/user/"
                        + "remove/avatar/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final StatusMessageDto infoDto = jsonConverter.fromJson(infoString, StatusMessageDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void removeGalleryImage(int galleryIdx, OnRequestResult<StatusMessageDto> listener) {

        HttpRequest httpRequest = new HttpRequest(Request.Method.POST,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/classinfo/"
                        + "remove/gallery/" + galleryIdx,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final StatusMessageDto infoDto = jsonConverter.fromJson(infoString, StatusMessageDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void getGallery(OnRequestResult<TrainerGalleryDto> listener) {

        HttpRequest httpRequest = new HttpRequest(Request.Method.GET,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/classinfo/"
                        + "get/gallery",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final TrainerGalleryDto infoDto = jsonConverter.fromJson(infoString, TrainerGalleryDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void getUserDetails(Long userId, OnRequestResult<UserInfoDto> listener) {

        HttpRequest httpRequest = new HttpRequest(Request.Method.GET,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/user/"
                        + "get/details/" + userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final UserInfoDto infoDto = jsonConverter.fromJson(infoString, UserInfoDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void getCertificates(OnRequestResult<TrainerCertificateListDto> listener) {

        HttpRequest httpRequest = new HttpRequest(Request.Method.GET,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/classinfo/"
                        + "get/certificates",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final TrainerCertificateListDto infoDto = jsonConverter.fromJson(infoString, TrainerCertificateListDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void removeCertificate(Long id, OnRequestResult<StatusMessageDto> listener) {

        HttpRequest httpRequest = new HttpRequest(Request.Method.POST,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/classinfo/"
                        + "remove/certificate/" + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final StatusMessageDto infoDto = jsonConverter.fromJson(infoString, StatusMessageDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void getPilatesTypesSelection(OnRequestResult<SelectionListDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.GET,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/classinfo/"
                        + "get/selection/pilates-types",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final SelectionListDto infoDto = jsonConverter.fromJson(infoString, SelectionListDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void getPilatesTypes(OnRequestResult<SelectionListDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.GET,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/classinfo/"
                        + "get/pilates-types",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final SelectionListDto infoDto = jsonConverter.fromJson(infoString, SelectionListDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void savePilatesTypes(SelectionListDto data, OnRequestResult<StatusMessageDto> listener) {

        HttpRequest httpRequest = new HttpRequest(Request.Method.POST,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/classinfo/"
                        + "save/pilates-types",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final StatusMessageDto infoDto = jsonConverter.fromJson(infoString, StatusMessageDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, data, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void saveAboutBio(AboutBioDto data, OnRequestResult<StatusMessageDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.POST,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/classinfo/"
                        + "save/aboutbio",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final StatusMessageDto infoDto = jsonConverter.fromJson(infoString, StatusMessageDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, data, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void saveTraineeSignup(TraineeSignupInfoDto data, OnRequestResult<StatusMessageDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.POST,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/classinfo/"
                        + "save/trainee-signup",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final StatusMessageDto infoDto = jsonConverter.fromJson(infoString, StatusMessageDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, data, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void findTrainerByDiscountCode(String code, OnRequestResult<UserInfoDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.GET,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/classinfo/"
                        + "find/discount-code/" + code,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final UserInfoDto infoDto = jsonConverter.fromJson(infoString, UserInfoDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void findTrainersByName(String name, OnRequestResult<UserDetailListDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.GET,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/classinfo/"
                        + "find/trainer-name/" + name,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final UserDetailListDto infoDto = jsonConverter.fromJson(infoString, UserDetailListDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void getClassSelections(OnRequestResult<AddClassSelectionsDto> listener) {

        HttpRequest httpRequest = new HttpRequest(Request.Method.GET,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/class/"
                        + "get/selections",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final AddClassSelectionsDto infoDto = jsonConverter.fromJson(infoString, AddClassSelectionsDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void getClassSpecification(Long id, OnRequestResult<ClassSpecDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.GET,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/class/"
                        + "get/specs/" + (id != null ? id : ""),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final ClassSpecDto infoDto = jsonConverter.fromJson(infoString, ClassSpecDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void saveClassSpec(ClassSpecDto data, OnRequestResult<StatusMessageDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.POST,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/class/"
                        + "save/specs",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final StatusMessageDto infoDto = jsonConverter.fromJson(infoString, StatusMessageDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, data, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void getClassTiming(Long id, OnRequestResult<ClassTimingDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.GET,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/class/"
                        + "get/timing/" + (id != null ? id : ""),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final ClassTimingDto infoDto = jsonConverter.fromJson(infoString, ClassTimingDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void saveClassTiming(ClassTimingDto data, OnRequestResult<StatusMessageDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.POST,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/class/"
                        + "save/timing",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final StatusMessageDto infoDto = jsonConverter.fromJson(infoString, StatusMessageDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, data, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void getClassDetails(Long id, OnRequestResult<ClassDetailsDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.GET,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/class/"
                        + "get/details/" + (id != null ? id : ""),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final ClassDetailsDto infoDto = jsonConverter.fromJson(infoString, ClassDetailsDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void saveClassDetails(ClassDetailsDto data, OnRequestResult<StatusMessageDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.POST,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/class/"
                        + "save/details",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final StatusMessageDto infoDto = jsonConverter.fromJson(infoString, StatusMessageDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, data, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void getClassGallery(Long classId, OnRequestResult<TrainerGalleryDto> listener) {

        HttpRequest httpRequest = new HttpRequest(Request.Method.GET,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/class/"
                        + "get/gallery/" + classId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final TrainerGalleryDto infoDto = jsonConverter.fromJson(infoString, TrainerGalleryDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void removeClassGalleryImage(int galleryIdx, OnRequestResult<StatusMessageDto> listener) {

        HttpRequest httpRequest = new HttpRequest(Request.Method.POST,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/class/"
                        + "remove/gallery/" + galleryIdx,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final StatusMessageDto infoDto = jsonConverter.fromJson(infoString, StatusMessageDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void getFullClassInfo(Long id, boolean draftMode, OnRequestResult<ClassFullInfoDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.GET,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/class/"
                        + "get/" + (draftMode ? "draft" : "info") + "/" + (id != null ? id : ""),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final ClassFullInfoDto infoDto = jsonConverter.fromJson(infoString, ClassFullInfoDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void publishClass(Long classId, OnRequestResult<StatusMessageDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.POST,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/class/"
                        + "publish/" + classId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final StatusMessageDto infoDto = jsonConverter.fromJson(infoString, StatusMessageDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void getTrainerClassSetupsList(int page, OnRequestResult<ClassFilteredListDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.GET,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/class/"
                        + "get/trainer-setups/" + page,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final ClassFilteredListDto infoDto = jsonConverter.fromJson(infoString, ClassFilteredListDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void cloneClass(Long classId, OnRequestResult<StatusMessageDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.POST,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/class/"
                        + "clone/" + classId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final StatusMessageDto infoDto = jsonConverter.fromJson(infoString, StatusMessageDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void getClassList(ClassFilterRequest filters, OnRequestResult<ClassFilteredListDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.GET,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/class/"
                        + "get/all?page=" + filters.getPage() + "&mode=" + filters.getMode(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final ClassFilteredListDto infoDto = jsonConverter.fromJson(infoString, ClassFilteredListDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void getDashboardInfo(OnRequestResult<DashboardInfoDto> listener) {
        HttpRequest httpRequest = new HttpRequest(Request.Method.GET,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/class/"
                        + "get/dashboard",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final DashboardInfoDto infoDto = jsonConverter.fromJson(infoString, DashboardInfoDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void cancelClass(Long classId, OnRequestResult<StatusMessageDto> listener) {

        HttpRequest httpRequest = new HttpRequest(Request.Method.POST,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/class/"
                        + "cancel/" + classId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final StatusMessageDto infoDto = jsonConverter.fromJson(infoString, StatusMessageDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }

    public void registerForClass(Long classId, OnRequestResult<StatusMessageDto> listener) {

        HttpRequest httpRequest = new HttpRequest(Request.Method.POST,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/class/"
                        + "register-for/" + classId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        final StatusMessageDto infoDto = jsonConverter.fromJson(infoString, StatusMessageDto.class);
                        listener.requestComplete(infoDto);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.requestError(error);
                    }
                }, null, context.getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", ""));

        requestQueue.add(httpRequest);
    }
}
