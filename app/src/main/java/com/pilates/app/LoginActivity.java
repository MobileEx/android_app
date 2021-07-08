package com.pilates.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.pilates.app.controls.DateTimeSelector;
import com.pilates.app.controls.listeners.OnDateTimePickedListener;
import com.pilates.app.controls.listeners.OnRemoveByIdListener;
import com.pilates.app.controls.listeners.OnTrainerSelectedOperation;
import com.pilates.app.controls.screens.AvatarSetup;
import com.pilates.app.controls.screens.CertificatesSetup;
import com.pilates.app.controls.screens.GallerySetup;
import com.pilates.app.controls.screens.InviteDiscountSetup;
import com.pilates.app.controls.screens.PilatesTypesSetup;
import com.pilates.app.controls.RoundButton;
import com.pilates.app.controls.RoundButtonGroup;
import com.pilates.app.controls.SlidingPanel;
import com.pilates.app.controls.listeners.OnOperationCompleteListener;
import com.pilates.app.controls.listeners.OnRoundButtonClickListener;
import com.pilates.app.model.HttpRequest;
import com.pilates.app.model.UserRole;
import com.pilates.app.model.UserSession;
import com.pilates.app.model.dto.AboutBioDto;
import com.pilates.app.model.dto.BasicProfileDto;
import com.pilates.app.model.dto.ExternalLoginInfoDto;
import com.pilates.app.model.dto.LoginResponseDto;
import com.pilates.app.model.dto.PortfolioDto;
import com.pilates.app.model.dto.StatusMessageDto;
import com.pilates.app.model.dto.TraineeSignupInfoDto;
import com.pilates.app.model.dto.UserDto;
import com.pilates.app.model.dto.enums.ClassPurpose;
import com.pilates.app.model.dto.enums.ExperienceLevel;
import com.pilates.app.service.UserDataService;
import com.pilates.app.service.listener.OnRequestResult;
import com.pilates.app.util.Constant;
import com.pilates.app.util.DefaultOperations;
import com.pilates.app.ws.SignalingWebSocket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 5;
    private final UserRegistry registry = UserRegistry.getInstance();
    private final Gson jsonConverter = new Gson();
    private RequestQueue requestQueue;
    private UserDto dto;

    private RoundButton loginButton;
    private EditText etEmail;
    private EditText etPassword;
    private RoundButton tvSignUp;
    private CallbackManager callbackManager;
    private TextView tvErrorMessage;
    private GoogleSignInClient mGoogleSignInClient;
    private UserRole signupRole;
    private RoundButton signupButton;

    private BasicProfileDto trainerBasicProfile;
    private TraineeSignupInfoDto traineeSignupInfo;

    private Spinner spnYears;
    private int currentScreen;
    private EditText signupName;
    private EditText signupEmail;
    private EditText signupPassword;
    private EditText signupConfirmPassword;
    private TextView tvSignupErrorMessage;
    private TextView tvScreenTitle;
    private ArrayList<TextView> stepBullets;
    private Spinner spnCountry;
    private ArrayList<String> countries;
    private EditText etPhone;
    private EditText etPostcode;
    private EditText etAddress;
    private TextView tvInfo;
    private TextView tvTerms;
    private TextView tvTerms2;
    private TextView tvAboutMeChars;
    private EditText profileAboutMe;
    private GallerySetup gallerySetup;
    private AvatarSetup avatarSetup;

    private UserDataService userService;
    private CertificatesSetup certificateSetup;
    private PilatesTypesSetup pilatesTypesSetup;
    private InviteDiscountSetup discountSetup;
    private TextView tvSkip;
    private SlidingPanel panelHelp;
    private LearnMoreType currentLMType;

    private DateTimeSelector dtsLogin;

    public enum LearnMoreType {
        MENU,
        HOW, WHY
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userService = new UserDataService(this);
        trainerBasicProfile = new BasicProfileDto();
        trainerBasicProfile.setTrainerClassDurations(new ArrayList<>());
        trainerBasicProfile.setAvailablePartOfDay(new ArrayList<>());

        traineeSignupInfo = new TraineeSignupInfoDto();
        traineeSignupInfo.setClassDurations(new ArrayList<>());
        traineeSignupInfo.setAvailablePartOfDay(new ArrayList<>());
        traineeSignupInfo.setGoals(new ArrayList<>());

        requestQueue = Volley.newRequestQueue(this);

        tvSignUp = findViewById(R.id.rbRegister);
        panelHelp = findViewById(R.id.panelHelp);
        panelHelp.setHeightMod(0.2f);

        // login fields
        loginButton = findViewById(R.id.rbContinue);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        tvErrorMessage.setVisibility(View.GONE);

        // signup fields
        signupButton = findViewById(R.id.rbSignupContinue);
        spnYears = findViewById(R.id.spnYears);
        signupName = findViewById(R.id.signupName);
        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);
        signupConfirmPassword = findViewById(R.id.signupConfirmPassword);
        tvSignupErrorMessage = findViewById(R.id.tvSignupErrorMessage);
        tvSignupErrorMessage.setVisibility(View.GONE);
        tvScreenTitle = findViewById(R.id.tvScreenTitle);
        spnCountry = findViewById(R.id.spnCountry);
        etPhone = findViewById(R.id.accountPhone);
        etPostcode = findViewById(R.id.accountPostCode);
        etAddress = findViewById(R.id.accountAddress);
        tvInfo = findViewById(R.id.tvInfo);
        tvTerms = findViewById(R.id.tvTerms);
        tvTerms2 = findViewById(R.id.tvTerms2);

        tvSkip = findViewById(R.id.tvSkip);
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.frameScreens).setVisibility(View.GONE);
                findViewById(R.id.frameLearnMore).setVisibility(View.GONE);
                findViewById(R.id.frameWelcome).setVisibility(View.VISIBLE);
                findViewById(R.id.layoutWelcomeAction).setVisibility(View.GONE);
                findViewById(R.id.layoutWelcomeStart).setVisibility(View.VISIBLE);

                DefaultOperations.loginRegisterFlow(getActivity());
            }
        });
        tvSkip.setVisibility(View.GONE);

        tvInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentScreen == Constant.LoginScreens.BASIC_COMPLETE) {
                    findViewById(R.id.frameScreens).setVisibility(View.GONE);
                    findViewById(R.id.frameLearnMore).setVisibility(View.GONE);
                    findViewById(R.id.frameWelcome).setVisibility(View.VISIBLE);
                    findViewById(R.id.layoutWelcomeAction).setVisibility(View.GONE);
                    findViewById(R.id.layoutWelcomeStart).setVisibility(View.VISIBLE);

                    DefaultOperations.loginRegisterFlow(getActivity());
                }
                else
                    panelHelp.showPanel();
            }
        });

        // profile
        tvAboutMeChars = findViewById(R.id.tvAboutMeCharacters);
        profileAboutMe = findViewById(R.id.profileAboutMe);
        profileAboutMe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvAboutMeChars.setText("Characters " + s.length() + "/1000");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dtsLogin = findViewById(R.id.dtsLogin);
        dtsLogin.setVisibility(View.GONE);

        gallerySetup = findViewById(R.id.layoutGallery);
        gallerySetup.setErrorTextView(tvSignupErrorMessage);

        avatarSetup = findViewById(R.id.layoutProfilePic);
        avatarSetup.init(tvSignupErrorMessage, false);

        certificateSetup = findViewById(R.id.layoutCertificates);
        certificateSetup.init(tvSignupErrorMessage, signupButton, dtsLogin);

        pilatesTypesSetup = findViewById(R.id.layoutPilatesTypes);
        pilatesTypesSetup.init(tvSignupErrorMessage, signupButton);
        pilatesTypesSetup.setCompleteListener(new OnOperationCompleteListener() {
            @Override
            public void complete() {
                navigateScreen(currentScreen + 1);
            }
        });

        discountSetup = findViewById(R.id.layoutTraineeInstructor);
        discountSetup.init(tvSignupErrorMessage, signupButton);
        discountSetup.setSelectedTrainerListener(new OnTrainerSelectedOperation() {
            @Override
            public void selected(Long id) {
                traineeSignupInfo.setTrainerId(id);
            }

            @Override
            public void removed() {
                traineeSignupInfo.setTrainerId(null);
            }
        });

        // spinner years
        ArrayList<String> years = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            years.add((LocalDateTime.now().getYear() - i) + "");
        }
        ArrayAdapter<String> adapterYears = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, years);
        adapterYears.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnYears.setAdapter(adapterYears);
        spnYears.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                trainerBasicProfile.setStartTrainYear(LocalDateTime.now().getYear() - position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // spn countries
        countries = new ArrayList<>();
        countries.add("Bulgaria");
        countries.add("Ireland");

        ArrayAdapter<String> adapterCountry = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, countries);
        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCountry.setAdapter(adapterCountry);
        spnCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                trainerBasicProfile.setCountry(countries.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stepBullets = new ArrayList<>();
        stepBullets.add(findViewById(R.id.tvStep1));
        stepBullets.add(findViewById(R.id.tvStep2));
        stepBullets.add(findViewById(R.id.tvStep3));
        stepBullets.add(findViewById(R.id.tvStep4));
        stepBullets.add(findViewById(R.id.tvStep5));
        stepBullets.add(findViewById(R.id.tvStep6));

        tvSignUp.setOnClickListener(v -> startActivity(new Intent(this, RegistrationActivity.class)));
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                if (validate()) {
                    loginButton.setLoading(true);

                    dto = UserDto.newBuilder()
                            .withEmail(email)
                            .withPassword(password)
                            .build();

                    registry.saveDto(dto);

                    tvErrorMessage.setVisibility(View.GONE);

                    userService.login(dto, new OnRequestResult<LoginResponseDto>() {
                        @Override
                        public void requestComplete(LoginResponseDto infoDto) {
                            loginButton.setLoading(false);
                            if (Objects.nonNull(infoDto.getError()) && infoDto.getError()) {
                                tvErrorMessage.setVisibility(View.VISIBLE);
                                tvErrorMessage.setText(infoDto.getMessage());
                            } else {
                                final UserSession userSession = new UserSession(infoDto.getId(), infoDto.getName(), infoDto.getRole());
                                registry.saveUser(userSession);

                                SharedPreferences preferences = getSharedPreferences("woobody", MODE_PRIVATE);
                                SharedPreferences.Editor edit = preferences.edit();
                                edit.putString("accessToken", infoDto.getAccessToken());
                                edit.putLong("userId", infoDto.getId());
                                edit.putString("name", infoDto.getName());
                                edit.putString("role", infoDto.getRole().toString());
                                edit.commit();

                                findViewById(R.id.frameScreens).setVisibility(View.GONE);
                                findViewById(R.id.frameLearnMore).setVisibility(View.GONE);
                                findViewById(R.id.frameWelcome).setVisibility(View.VISIBLE);
                                findViewById(R.id.layoutWelcomeAction).setVisibility(View.GONE);
                                findViewById(R.id.layoutWelcomeStart).setVisibility(View.VISIBLE);

                                DefaultOperations.loginRegisterFlow(getActivity());
                            }
                        }

                        @Override
                        public void requestError(VolleyError ex) {
                            loginButton.setLoading(false);
                            tvErrorMessage.setVisibility(View.VISIBLE);
                            tvErrorMessage.setText("An unexpected error has occurred.");
                        }
                    });
                }
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        findViewById(R.id.frameScreens).setVisibility(View.GONE);
        findViewById(R.id.frameLearnMore).setVisibility(View.GONE);
        findViewById(R.id.frameWelcome).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutWelcomeAction).setVisibility(View.GONE);
        findViewById(R.id.layoutWelcomeStart).setVisibility(View.VISIBLE);

        findViewById(R.id.rbLearnMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.frameWelcome).setVisibility(View.GONE);
                findViewById(R.id.frameLearnMore).setVisibility(View.VISIBLE);
                showLearnMore(LearnMoreType.MENU);
            }
        });

        findViewById(R.id.imgIconBackLM).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLMType == LearnMoreType.MENU) {
                    findViewById(R.id.frameWelcome).setVisibility(View.VISIBLE);
                    findViewById(R.id.frameLearnMore).setVisibility(View.GONE);
                }
                else {
                    showLearnMore(LearnMoreType.MENU);
                }
            }
        });

        findViewById(R.id.hbHow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLearnMore(LearnMoreType.HOW);
            }
        });

        findViewById(R.id.hbWhy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLearnMore(LearnMoreType.WHY);
            }
        });

        findViewById(R.id.rbGetStarted).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.layoutWelcomeAction).setVisibility(View.VISIBLE);
                findViewById(R.id.layoutWelcomeStart).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.rbLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.frameScreens).setVisibility(View.VISIBLE);
                findViewById(R.id.frameWelcome).setVisibility(View.GONE);
                navigateScreen(Constant.LoginScreens.LOGIN);
            }
        });

        findViewById(R.id.imgIconBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentScreen == Constant.LoginScreens.LOGIN || currentScreen == Constant.LoginScreens.PRESIGNUP) {
                    findViewById(R.id.frameScreens).setVisibility(View.GONE);
                    findViewById(R.id.frameWelcome).setVisibility(View.VISIBLE);
                }
                else {
                    navigateScreen(currentScreen - 1);
                }
            }
        });

        findViewById(R.id.rbRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupRole = UserRole.TRAINEE;
                panelHelp.setHelpText(getResources().getString(R.string.label_help_signup_trainee));

                findViewById(R.id.frameScreens).setVisibility(View.VISIBLE);
                findViewById(R.id.frameWelcome).setVisibility(View.GONE);
                navigateScreen(Constant.LoginScreens.PRESIGNUP);
            }
        });

        findViewById(R.id.rbRegisterTrainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupRole = UserRole.TRAINER;

                panelHelp.setHelpText(getResources().getString(R.string.label_help_signup));
                findViewById(R.id.frameScreens).setVisibility(View.VISIBLE);
                findViewById(R.id.frameWelcome).setVisibility(View.GONE);
                navigateScreen(Constant.LoginScreens.PRESIGNUP);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate())
                    return;

                if (currentScreen == Constant.LoginScreens.SIGNUP) {
                    UserDto dto = UserDto.newBuilder()
                            .withEmail(signupEmail.getText().toString())
                            .withPassword(signupPassword.getText().toString())
                            .withRole(signupRole)
                            .withConfirmPassword(signupConfirmPassword.getText().toString())
                            .withName(signupName.getText().toString())
                            .build();

                    register(dto);
                } else if (currentScreen == Constant.LoginScreens.ACCOUNT_DETAILS) {
                    saveBasicProfile();
                } else if (currentScreen == Constant.LoginScreens.PILATES_TYPES) {
                    pilatesTypesSetup.saveTypes();
                }
                else if (currentScreen == Constant.LoginScreens.COMPLETE || currentScreen == Constant.LoginScreens.TRAINEE_COMPLETE) {
                    findViewById(R.id.frameScreens).setVisibility(View.GONE);
                    findViewById(R.id.frameLearnMore).setVisibility(View.GONE);
                    findViewById(R.id.frameWelcome).setVisibility(View.VISIBLE);
                    findViewById(R.id.layoutWelcomeAction).setVisibility(View.GONE);
                    findViewById(R.id.layoutWelcomeStart).setVisibility(View.VISIBLE);

                    DefaultOperations.loginRegisterFlow(getActivity());
                }
                else if (currentScreen == Constant.LoginScreens.ABOUT_BIO) {
                    tvSignupErrorMessage.setVisibility(View.GONE);
                    signupButton.setLoading(true);

                    AboutBioDto info = new AboutBioDto();
                    info.setText(profileAboutMe.getText().toString());
                    userService.saveAboutBio(info, new OnRequestResult<StatusMessageDto>() {
                        @Override
                        public void requestComplete(StatusMessageDto data) {
                            signupButton.setLoading(false);

                            if(!data.getError())
                                navigateScreen(currentScreen + 1);
                            else {
                                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                                tvSignupErrorMessage.setText(data.getMessage());
                            }
                        }

                        @Override
                        public void requestError(VolleyError ex) {
                            signupButton.setLoading(false);

                            tvSignupErrorMessage.setVisibility(View.VISIBLE);
                            tvSignupErrorMessage.setText("Couldn't save info.");
                        }
                    });
                }
                else if (currentScreen == Constant.LoginScreens.TRAINEE_INSTRUCTOR) {
                    tvSignupErrorMessage.setVisibility(View.GONE);
                    signupButton.setLoading(true);

                    userService.saveTraineeSignup(traineeSignupInfo, new OnRequestResult<StatusMessageDto>() {
                        @Override
                        public void requestComplete(StatusMessageDto data) {
                            signupButton.setLoading(false);

                            if(!data.getError())
                                navigateScreen(currentScreen + 1);
                            else {
                                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                                tvSignupErrorMessage.setText(data.getMessage());
                            }
                        }

                        @Override
                        public void requestError(VolleyError ex) {
                            signupButton.setLoading(false);

                            tvSignupErrorMessage.setVisibility(View.VISIBLE);
                            tvSignupErrorMessage.setText("Couldn't save info.");
                        }
                    });
                }

                else
                    navigateScreen(currentScreen + 1);
            }
        });

        ((RoundButtonGroup) findViewById(R.id.rbgSelectTimesPerWeek)).setButtonListener(new OnRoundButtonClickListener() {
            @Override
            public void clicked(RoundButton button) {
                if(signupRole == UserRole.TRAINER)
                    trainerBasicProfile.setAvailableTimePerWeek(Integer.parseInt(button.getValue()));
                else
                    traineeSignupInfo.setAvailableTimePerWeek(Integer.parseInt(button.getValue()));
            }

            @Override
            public void removed(RoundButton button) {

            }
        });

        ((RoundButtonGroup) findViewById(R.id.rbgSelectClassDurations)).setButtonListener(new OnRoundButtonClickListener() {
            @Override
            public void clicked(RoundButton button) {
                if(signupRole == UserRole.TRAINER)
                    trainerBasicProfile.getTrainerClassDurations().add(Integer.parseInt(button.getValue()));
                else
                    traineeSignupInfo.getClassDurations().add(Integer.parseInt(button.getValue()));
            }

            @Override
            public void removed(RoundButton button) {
                if(signupRole == UserRole.TRAINER)
                    trainerBasicProfile.getTrainerClassDurations().remove((Object) Integer.parseInt(button.getValue()));
                else
                    traineeSignupInfo.getClassDurations().remove((Object)Integer.parseInt(button.getValue()));
            }
        });

        ((RoundButtonGroup) findViewById(R.id.rbgSelectTimeOfDay)).setButtonListener(new OnRoundButtonClickListener() {
            @Override
            public void clicked(RoundButton button) {
                if(signupRole == UserRole.TRAINER)
                    trainerBasicProfile.getAvailablePartOfDay().add(PortfolioDto.DayPart.valueOf(button.getValue()));
                else
                    traineeSignupInfo.getAvailablePartOfDay().add(PortfolioDto.DayPart.valueOf(button.getValue()));
            }

            @Override
            public void removed(RoundButton button) {
                if(signupRole == UserRole.TRAINER)
                    trainerBasicProfile.getAvailablePartOfDay().remove(PortfolioDto.DayPart.valueOf(button.getValue()));
                else
                    traineeSignupInfo.getAvailablePartOfDay().remove(PortfolioDto.DayPart.valueOf(button.getValue()));
            }
        });

        ((RoundButtonGroup) findViewById(R.id.rbgSelectTraineeGoals)).setButtonListener(new OnRoundButtonClickListener() {
            @Override
            public void clicked(RoundButton button) {
                    traineeSignupInfo.getGoals().add(ClassPurpose.valueOf(button.getValue()));
            }

            @Override
            public void removed(RoundButton button) {
                traineeSignupInfo.getGoals().remove(ClassPurpose.valueOf(button.getValue()));
            }
        });

        ((RoundButtonGroup) findViewById(R.id.rbgSelectTraineeExpLevel)).setButtonListener(new OnRoundButtonClickListener() {
            @Override
            public void clicked(RoundButton button) {
                traineeSignupInfo.setExperienceLevel(ExperienceLevel.valueOf(button.getValue()));
            }

            @Override
            public void removed(RoundButton button) {
            }
        });

        navigateScreen(Constant.LoginScreens.SIGNUP);

        // set fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");
        Typeface fontBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Bold.ttf");
        Typeface fontRegular = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");

        // general
        ((TextView) findViewById(R.id.tvScreenTitle)).setTypeface(font);
        tvInfo.setTypeface(fontRegular);
        tvTerms.setTypeface(fontRegular);
        tvTerms2.setTypeface(fontRegular);
        tvSkip.setTypeface(fontRegular);
        ((TextView) findViewById(R.id.tvLMTitle)).setTypeface(fontBold);
        ((TextView) findViewById(R.id.tvLMText)).setTypeface(fontRegular);

        // login
        ((TextView) findViewById(R.id.tvLoginHeader)).setTypeface(font);
        ((TextView) findViewById(R.id.tvEmail)).setTypeface(font);
        ((TextView) findViewById(R.id.tvPassword)).setTypeface(font);

        // signup
        ((TextView) findViewById(R.id.tvPreSignupHeader)).setTypeface(font);
        ((TextView) findViewById(R.id.tvSignupName)).setTypeface(font);
        ((TextView) findViewById(R.id.tvSignupEmail)).setTypeface(font);
        ((TextView) findViewById(R.id.tvSignupPassword)).setTypeface(font);
        ((TextView) findViewById(R.id.tvSignupErrorMessage)).setTypeface(font);
        ((TextView) findViewById(R.id.tvSignupConfirmPassword)).setTypeface(font);
        ((TextView) findViewById(R.id.tvSignupHeader)).setTypeface(font);

        // exp
        ((TextView) findViewById(R.id.tvExpHeader)).setTypeface(font);
        ((TextView) findViewById(R.id.tvExpYears)).setTypeface(font);

        // tpw
        ((TextView) findViewById(R.id.tvTPWHeader)).setTypeface(font);

        // cd
        ((TextView) findViewById(R.id.tvCDHeader)).setTypeface(font);

        // tod
        ((TextView) findViewById(R.id.tvTODHeader)).setTypeface(font);

        // account details
        ((TextView) findViewById(R.id.tvAccountHeader)).setTypeface(font);
        ((TextView) findViewById(R.id.tvAccountCountry)).setTypeface(font);
        ((TextView) findViewById(R.id.tvAccountPhone)).setTypeface(font);
        ((TextView) findViewById(R.id.tvAccountPostCode)).setTypeface(font);

        // basic complete
        ((TextView) findViewById(R.id.tvCompleteBasicTitle)).setTypeface(fontBold);
        ((TextView) findViewById(R.id.tvCompleteBasicSubtitle)).setTypeface(font);
        ((TextView) findViewById(R.id.tvCompleteBasicSubtitle2)).setTypeface(font);

        // profile
        ((TextView) findViewById(R.id.tvAboutMeHeader)).setTypeface(font);
        tvAboutMeChars.setTypeface(font);

        // confirm details
        ((TextView) findViewById(R.id.tvConfirmationHeader)).setTypeface(font);
        ((TextView) findViewById(R.id.tvConfirmationInfo)).setTypeface(fontRegular);

        // complete
        ((TextView) findViewById(R.id.tvCompleteTitle)).setTypeface(fontBold);
        ((TextView) findViewById(R.id.tvCompleteSubtitle)).setTypeface(font);

        // trainee signup
        ((TextView) findViewById(R.id.tvTraineeExpLevelHeader)).setTypeface(font);
        ((TextView) findViewById(R.id.tvTraineeGoalsHeader)).setTypeface(font);

        ((TextView) findViewById(R.id.tvTraineeCompleteTitle)).setTypeface(fontBold);
        ((TextView) findViewById(R.id.tvTraineeCompleteSubtitle)).setTypeface(font);
        ((TextView) findViewById(R.id.tvTraineeCompleteSubtitle2)).setTypeface(fontBold);

        findViewById(R.id.frameLowerSocial).setVisibility(View.VISIBLE);
        tvErrorMessage.setTypeface(font);

        if(getSharedPreferences("woobody", MODE_PRIVATE).getString("accessToken", null) != null) {
            final UserSession userSession = new UserSession( getSharedPreferences("woobody", MODE_PRIVATE).getLong("userId", 0),
                    getSharedPreferences("woobody", MODE_PRIVATE).getString("name", ""),
                    UserRole.fromString( getSharedPreferences("woobody", MODE_PRIVATE).getString("role", "TRAINER")));
            registry.saveUser(userSession);
            startActivity(new Intent(this, DashboardActivity.class));
        }

        // facebook login stuff
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(
                callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // Handle success
                        loginButton.setLoading(true);
                        signupButton.setLoading(true);

                        ExternalLoginInfoDto loginInfo = new ExternalLoginInfoDto();
                        loginInfo.setProvider("Facebook");
                        loginInfo.setAccessToken(loginResult.getAccessToken().getToken());
                        loginInfo.setRole(UserRole.TRAINEE);

                        loginExternal(loginInfo);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getActivity(), "Login canceled", Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getActivity(), "Login failed: " + exception.toString(), Toast.LENGTH_LONG);
                    }
                }
        );

        findViewById(R.id.imgFbLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(
                        getActivity(),
                        Arrays.asList("email", "public_profile")
                );
            }
        });

        // google login stuff
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constant.WooConfigurations.GOOGLE_CLIENT_ID)
                .requestEmail()
                .requestProfile()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // check if user already signed with google
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        findViewById(R.id.imgGoogleLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });
    }

    private void showLearnMore(LearnMoreType type) {
        currentLMType = type;
        if(type == LearnMoreType.MENU) {
            findViewById(R.id.svLMContent).setVisibility(View.GONE);
            findViewById(R.id.layoutLMMenu).setVisibility(View.VISIBLE);
        }
        else if(type == LearnMoreType.HOW) {
            findViewById(R.id.layoutLMMenu).setVisibility(View.GONE);
            findViewById(R.id.svLMContent).setVisibility(View.VISIBLE);

            ((TextView) findViewById(R.id.tvLMTitle)).setText("How it works in a nutshell");
            ((TextView) findViewById(R.id.tvLMText)).setText("Text explaining the above goes here.");
        }
        else if(type == LearnMoreType.WHY) {
            findViewById(R.id.layoutLMMenu).setVisibility(View.GONE);
            findViewById(R.id.svLMContent).setVisibility(View.VISIBLE);

            ((TextView) findViewById(R.id.tvLMTitle)).setText("Why use Woobody");
            ((TextView) findViewById(R.id.tvLMText)).setText("Text explaining the above goes here.");
        }
    }

    private void navigateScreen(int screen) {
        currentScreen = screen;
        signupButton.setVisibility(View.VISIBLE);

        tvSkip.setVisibility(
                (screen > Constant.LoginScreens.BASIC_COMPLETE && screen < Constant.LoginScreens.COMPLETE)
                        ||
                        (screen >= Constant.LoginScreens.TRAINEE_EXPERIENCE_LEVEL
                                && screen < Constant.LoginScreens.TRAINEE_COMPLETE)? View.VISIBLE : View.GONE);
        tvScreenTitle.setText(screen < Constant.LoginScreens.PRESIGNUP ? "Login" :
                screen > Constant.LoginScreens.BASIC_COMPLETE
                        && screen < Constant.LoginScreens.TRAINEE_EXPERIENCE_LEVEL ? "Professional Details" : "Sign Up");

        findViewById(R.id.frameLogin).setVisibility(screen == Constant.LoginScreens.LOGIN ? View.VISIBLE : View.GONE);
        findViewById(R.id.frameSignUp).setVisibility(screen >= Constant.LoginScreens.PRESIGNUP ? View.VISIBLE : View.GONE);
        findViewById(R.id.frameLowerSocial).setVisibility(screen == Constant.LoginScreens.LOGIN || screen == Constant.LoginScreens.PRESIGNUP ? View.VISIBLE : View.GONE);
        findViewById(R.id.frameLowerSteps).setVisibility(screen != Constant.LoginScreens.LOGIN && screen != Constant.LoginScreens.PRESIGNUP ? View.VISIBLE : View.GONE);

        findViewById(R.id.layoutPreSignupStep).setVisibility(screen == Constant.LoginScreens.PRESIGNUP ? View.VISIBLE : View.GONE);
        findViewById(R.id.layoutSignup).setVisibility(screen == Constant.LoginScreens.SIGNUP ? View.VISIBLE : View.GONE);
        findViewById(R.id.layoutExperienceStep).setVisibility(screen == Constant.LoginScreens.EXPERIENCE ? View.VISIBLE : View.GONE);
        findViewById(R.id.layoutTimesPerWeekStep).setVisibility(screen == Constant.LoginScreens.TIMES_PER_WEEK
                || screen == Constant.LoginScreens.TRAINEE_TIMES_PER_WEEK? View.VISIBLE : View.GONE);
        findViewById(R.id.layoutClassDurations).setVisibility(screen == Constant.LoginScreens.CLASS_DURATIONS
                || screen == Constant.LoginScreens.TRAINEE_CLASS_DURATIONS ? View.VISIBLE : View.GONE);
        findViewById(R.id.layoutTimeOfDay).setVisibility(screen == Constant.LoginScreens.TIME_OF_DAY
                || screen == Constant.LoginScreens.TRAINEE_TIME_OF_DAY ? View.VISIBLE : View.GONE);
        findViewById(R.id.layoutAccountDetails).setVisibility(screen == Constant.LoginScreens.ACCOUNT_DETAILS ? View.VISIBLE : View.GONE);
        findViewById(R.id.layoutCompleteBasic).setVisibility(screen == Constant.LoginScreens.BASIC_COMPLETE ? View.VISIBLE : View.GONE);
        findViewById(R.id.layoutAboutMeStep).setVisibility(screen == Constant.LoginScreens.ABOUT_BIO ? View.VISIBLE : View.GONE);
        avatarSetup.setVisibility(screen == Constant.LoginScreens.PROFILE_PIC ? View.VISIBLE : View.GONE);
        gallerySetup.setVisibility(screen == Constant.LoginScreens.GALLERY ? View.VISIBLE : View.GONE);
        certificateSetup.setVisibility(screen == Constant.LoginScreens.DOCUMENTS ? View.VISIBLE : View.GONE);
        pilatesTypesSetup.setVisibility(screen == Constant.LoginScreens.PILATES_TYPES ? View.VISIBLE : View.GONE);
        findViewById(R.id.layoutConfirmation).setVisibility(screen == Constant.LoginScreens.CONFIRMATION ? View.VISIBLE : View.GONE);
        findViewById(R.id.layoutComplete).setVisibility(screen == Constant.LoginScreens.COMPLETE ? View.VISIBLE : View.GONE);

        findViewById(R.id.layoutTraineeExpLevel).setVisibility(screen == Constant.LoginScreens.TRAINEE_EXPERIENCE_LEVEL ? View.VISIBLE : View.GONE);
        findViewById(R.id.layoutTraineeGoals).setVisibility(screen == Constant.LoginScreens.TRAINEE_GOALS ? View.VISIBLE : View.GONE);
        discountSetup.setVisibility(screen == Constant.LoginScreens.TRAINEE_INSTRUCTOR ? View.VISIBLE : View.GONE);
        findViewById(R.id.layoutTraineeComplete).setVisibility(screen == Constant.LoginScreens.TRAINEE_COMPLETE ? View.VISIBLE : View.GONE);

        signupButton.setButtonBackground(screen == Constant.LoginScreens.TRAINEE_COMPLETE || screen == Constant.LoginScreens.BASIC_COMPLETE || screen == Constant.LoginScreens.COMPLETE ? R.drawable.button_bg_purple : R.drawable.button_bg_green);
        signupButton.setButtonTextColor(screen == Constant.LoginScreens.TRAINEE_COMPLETE || screen == Constant.LoginScreens.BASIC_COMPLETE || screen == Constant.LoginScreens.COMPLETE ? Color.WHITE : getResources().getColor(R.color.wooLabelGreen, null));
        signupButton.setText(screen == Constant.LoginScreens.COMPLETE ? "GO TO DASHBOARD" :
                screen == Constant.LoginScreens.CONFIRMATION ? "CONFIRM" :
                screen == Constant.LoginScreens.PRESIGNUP ? "CREATE MANUALLY" :
                screen == Constant.LoginScreens.BASIC_COMPLETE ? "COMPLETE TRAINER PROFILE" :
                        screen == Constant.LoginScreens.TRAINEE_COMPLETE ? "BROWSE CLASSES" :"CONTINUE");
        findViewById(R.id.layoutSteps).setVisibility(
                screen != Constant.LoginScreens.BASIC_COMPLETE
                        && screen != Constant.LoginScreens.TRAINEE_COMPLETE
                        && screen != Constant.LoginScreens.SIGNUP && screen != Constant.LoginScreens.COMPLETE ?
                        View.VISIBLE : View.GONE);

        tvInfo.setVisibility(screen < Constant.LoginScreens.COMPLETE ||
                (screen >= Constant.LoginScreens.TRAINEE_EXPERIENCE_LEVEL
                        && screen < Constant.LoginScreens.TRAINEE_COMPLETE) ? View.VISIBLE : View.GONE);
        tvInfo.setText(screen != Constant.LoginScreens.BASIC_COMPLETE ? getResources().getString(R.string.label_info)
                : getResources().getString(R.string.label_skip_profile));
        if (screen != Constant.LoginScreens.BASIC_COMPLETE && screen != Constant.LoginScreens.TRAINEE_COMPLETE) {
            int step = screen >= Constant.LoginScreens.TRAINEE_EXPERIENCE_LEVEL ? Constant.LoginScreens.TRAINEE_EXPERIENCE_LEVEL - 1 : screen < Constant.LoginScreens.BASIC_COMPLETE ? 2 : Constant.LoginScreens.ABOUT_BIO - 1;
            for (TextView view : stepBullets) {
                view.setBackgroundResource(step == screen - 1 ? R.drawable.bullet_bg_active : R.drawable.bullet_bg_faded);
                step++;
            }
        }
    }

    private void loginExternal(ExternalLoginInfoDto loginInfo) {
        tvErrorMessage.setVisibility(View.GONE);
        tvSignupErrorMessage.setVisibility(View.GONE);

        HttpRequest httpRequest = new HttpRequest(Request.Method.POST,
                Constant.ServiceEndpoints.USER_MANAGEMENT + "/user-management/api/v1/user/external-login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String infoString) {
                        loginButton.setLoading(false);
                        signupButton.setLoading(false);

                        final LoginResponseDto infoDto = jsonConverter.fromJson(infoString, LoginResponseDto.class);

                        if (Objects.nonNull(infoDto.getError()) && infoDto.getError()) {
                            tvErrorMessage.setVisibility(View.VISIBLE);
                            tvErrorMessage.setText(infoDto.getMessage());

                            tvSignupErrorMessage.setVisibility(View.GONE);
                            tvSignupErrorMessage.setText(infoDto.getMessage());
                        } else {
                            final UserSession userSession = new UserSession(infoDto.getId(), infoDto.getName(), infoDto.getRole());
                            registry.saveUser(userSession);

                            SharedPreferences preferences = getSharedPreferences("woobody", MODE_PRIVATE);
                            SharedPreferences.Editor edit = preferences.edit();
                            edit.putString("accessToken", infoDto.getAccessToken());
                            edit.putLong("userId", infoDto.getId());
                            edit.putString("name", infoDto.getName());
                            edit.putString("role", infoDto.getRole().toString());
                            edit.commit();

                            trainerBasicProfile.setExternal(true);

                            if(currentScreen == Constant.LoginScreens.LOGIN || infoDto.getHasAccount()) {
                                findViewById(R.id.frameScreens).setVisibility(View.GONE);
                                findViewById(R.id.frameLearnMore).setVisibility(View.GONE);
                                findViewById(R.id.frameWelcome).setVisibility(View.VISIBLE);
                                findViewById(R.id.layoutWelcomeAction).setVisibility(View.GONE);
                                findViewById(R.id.layoutWelcomeStart).setVisibility(View.VISIBLE);

                                DefaultOperations.loginRegisterFlow(getActivity());
                            }
                            else if(currentScreen == Constant.LoginScreens.PRESIGNUP)
                                navigateScreen(signupRole == UserRole.TRAINER ? Constant.LoginScreens.EXPERIENCE : Constant.LoginScreens.TRAINEE_EXPERIENCE_LEVEL);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        signupButton.setLoading(false);
                        loginButton.setLoading(false);
                        error.printStackTrace();
                        tvErrorMessage.setVisibility(View.VISIBLE);
                        tvErrorMessage.setText("An unexpected error has occurred.");

                        tvSignupErrorMessage.setVisibility(View.GONE);
                        tvSignupErrorMessage.setText("An unexpected error has occurred.");

                        System.out.println("ERROR WHILE LOGIN" + error.getMessage());
                    }
                }, loginInfo);

        requestQueue.add(httpRequest);
    }

    private void register(UserDto registerInfo) {
        signupButton.setLoading(true);
        registerInfo.setRole(signupRole);

        userService.register(registerInfo, new OnRequestResult<LoginResponseDto>() {
            @Override
            public void requestComplete(LoginResponseDto infoDto) {
                signupButton.setLoading(false);
                if (Objects.nonNull(infoDto.getError()) && infoDto.getError()) {
                    tvSignupErrorMessage.setVisibility(View.VISIBLE);
                    tvSignupErrorMessage.setText(infoDto.getMessage());
                } else {
                    if (signupRole == UserRole.TRAINEE) {
                        final UserSession userSession = new UserSession(infoDto.getId(), infoDto.getName(), infoDto.getRole());
                        registry.saveUser(userSession);

                        SharedPreferences preferences = getSharedPreferences("woobody", MODE_PRIVATE);
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putString("accessToken", infoDto.getAccessToken());
                        edit.putLong("userId", infoDto.getId());
                        edit.putString("name", infoDto.getName());
                        edit.putString("role", infoDto.getRole().toString());
                        edit.commit();

                        navigateScreen(Constant.LoginScreens.TRAINEE_EXPERIENCE_LEVEL);
                    } else {
                        trainerBasicProfile.setEmail(registerInfo.getEmail());
                        trainerBasicProfile.setPassword(registerInfo.getPassword());
                        trainerBasicProfile.setName(registerInfo.getName());
                        navigateScreen(Constant.LoginScreens.EXPERIENCE);
                    }
                }
            }

            @Override
            public void requestError(VolleyError ex) {
                signupButton.setLoading(false);
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("An unexpected error has occurred.");
            }
        });
    }

    private void saveBasicProfile() {
        signupButton.setLoading(true);

        trainerBasicProfile.setPhone(etPhone.getText().toString());
        trainerBasicProfile.setAddress(etAddress.getText().toString());
        trainerBasicProfile.setPostCode(etPostcode.getText().toString());

        userService.saveBasicProfile(trainerBasicProfile, new OnRequestResult<LoginResponseDto>() {
            @Override
            public void requestComplete(LoginResponseDto infoDto) {
                signupButton.setLoading(false);
                if (Objects.nonNull(infoDto.getError()) && infoDto.getError()) {
                    tvSignupErrorMessage.setVisibility(View.VISIBLE);
                    tvSignupErrorMessage.setText(infoDto.getMessage());
                } else {
                    final UserSession userSession = new UserSession(infoDto.getId(), infoDto.getName(), infoDto.getRole());
                    registry.saveUser(userSession);

                    if(!trainerBasicProfile.isExternal()) {
                        SharedPreferences preferences = getSharedPreferences("woobody", MODE_PRIVATE);
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putString("accessToken", infoDto.getAccessToken());
                        edit.putLong("userId", infoDto.getId());
                        edit.putString("name", infoDto.getName());
                        edit.putString("role", infoDto.getRole().toString());
                        edit.commit();
                    }

                    navigateScreen(Constant.LoginScreens.BASIC_COMPLETE);
                }
            }

            @Override
            public void requestError(VolleyError ex) {
                signupButton.setLoading(false);
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("An unexpected error has occurred.");
            }
        });
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        } else if ((requestCode == Constant.PickReason.AVATAR || requestCode == Constant.PickReason.GALLERY || requestCode == Constant.PickReason.DOCUMENT) && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            else if(requestCode == Constant.PickReason.GALLERY) {
                gallerySetup.doUploadImage(data);
            }
            else if(requestCode == Constant.PickReason.DOCUMENT) {
                certificateSetup.doUploadCert(data);
            }
            else
                avatarSetup.doUploadImage(data);
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            loginButton.setLoading(true);
            signupButton.setLoading(true);

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            ExternalLoginInfoDto loginInfo = new ExternalLoginInfoDto();
            loginInfo.setProvider("Google");
            loginInfo.setAccessToken(account.getIdToken());
            loginInfo.setRole(UserRole.TRAINEE);

            loginExternal(loginInfo);
        } catch (ApiException e) {
            loginButton.setLoading(false);
            tvErrorMessage.setVisibility(View.VISIBLE);
            tvErrorMessage.setText("Google login failed: " + e.getStatusCode() + "");
        }
    }

    private boolean validate() {
        tvErrorMessage.setVisibility(View.GONE);
        tvSignupErrorMessage.setVisibility(View.GONE);

        if (currentScreen == Constant.LoginScreens.LOGIN) {
            if (Objects.isNull(etEmail.getText().toString()) || Objects.equals(etEmail.getText().toString(), "")) {
                tvErrorMessage.setVisibility(View.VISIBLE);
                tvErrorMessage.setText("Enter email address.");
                return false;
            }

            if (Objects.isNull(etPassword.getText().toString()) || Objects.equals(etPassword.getText().toString(), "")) {
                tvErrorMessage.setVisibility(View.VISIBLE);
                tvErrorMessage.setText("Enter password.");
                return false;
            }
            return true;
        } else if (currentScreen == Constant.LoginScreens.PRESIGNUP)
            return true;
        else if (currentScreen == Constant.LoginScreens.SIGNUP) {
            if (Objects.isNull(signupName.getText().toString()) || Objects.equals(signupName.getText().toString(), "")) {
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("Enter your name.");
                return false;
            }

            if (Objects.isNull(signupEmail.getText().toString()) || Objects.equals(signupEmail.getText().toString(), "")) {
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("Enter email address.");
                return false;
            }

            if (Objects.isNull(signupPassword.getText().toString()) || Objects.equals(signupPassword.getText().toString(), "")) {
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("Enter password.");
                return false;
            }

            if (Objects.isNull(signupConfirmPassword.getText().toString()) || Objects.equals(signupConfirmPassword.getText().toString(), "")) {
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("Confirm password.");
                return false;
            }

            if (!Objects.equals(signupConfirmPassword.getText().toString(), signupPassword.getText().toString())) {
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("Passwords don't match.");
                return false;
            }
            return true;
        } else if (currentScreen == Constant.LoginScreens.EXPERIENCE)
            return true;
        else if (currentScreen == Constant.LoginScreens.TIMES_PER_WEEK || currentScreen == Constant.LoginScreens.TRAINEE_TIMES_PER_WEEK) {
            if ((signupRole == UserRole.TRAINER && trainerBasicProfile.getAvailableTimePerWeek() == null)
                || (signupRole == UserRole.TRAINEE && traineeSignupInfo.getAvailableTimePerWeek() == null)) {
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("Select times per week.");
                return false;
            }

            return true;
        } else if (currentScreen == Constant.LoginScreens.CLASS_DURATIONS || currentScreen == Constant.LoginScreens.TRAINEE_CLASS_DURATIONS) {
            if ((signupRole == UserRole.TRAINER && trainerBasicProfile.getTrainerClassDurations().size() == 0)
                || (signupRole == UserRole.TRAINEE && traineeSignupInfo.getClassDurations().size() == 0)) {
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("Select class durations.");
                return false;
            }

            return true;
        } else if (currentScreen == Constant.LoginScreens.TIME_OF_DAY || currentScreen == Constant.LoginScreens.TRAINEE_TIME_OF_DAY) {
            if ((signupRole == UserRole.TRAINER && trainerBasicProfile.getAvailablePartOfDay().size() == 0)
                || (signupRole == UserRole.TRAINEE && traineeSignupInfo.getAvailablePartOfDay().size() == 0)) {
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("Select time of day.");
                return false;
            }

            return true;
        } else if (currentScreen == Constant.LoginScreens.ACCOUNT_DETAILS) {
            if (trainerBasicProfile.getCountry() == null) {
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("Select country.");
                return false;
            } else if (etPhone.getText().toString() == null || etPhone.getText().toString().length() == 0) {
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("Enter your phone number.");
                return false;
            } else if (etPostcode.getText().toString() == null || etPostcode.getText().toString().length() == 0) {
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("Enter your postcode.");
                return false;
            } else if (etAddress.getText().toString() == null || etAddress.getText().toString().length() == 0) {
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("Enter your address.");
                return false;
            }

            return true;
        } else if (currentScreen == Constant.LoginScreens.BASIC_COMPLETE)
            return true;
        else if (currentScreen == Constant.LoginScreens.ABOUT_BIO) {
            if (profileAboutMe.getText() == null || profileAboutMe.getText().length() == 0) {
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("Enter your information.");
                return false;
            }

            return true;
        } else if (currentScreen == Constant.LoginScreens.PROFILE_PIC) {
            if(avatarSetup.getActiveURL() == null) {
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("Select an avatar.");
                return false;
            }
            return true;
        }
         else if (currentScreen == Constant.LoginScreens.GALLERY) {
             if(gallerySetup.getGalleryPaths().size() == 0) {
                 tvSignupErrorMessage.setVisibility(View.VISIBLE);
                 tvSignupErrorMessage.setText("Add at least one gallery image.");
                 return false;
             }
            return true;
        }
        else if (currentScreen == Constant.LoginScreens.DOCUMENTS) {
            if(certificateSetup.getAddedCerts() == 0) {
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("Add at least one document.");
                return false;
            }
            return true;
        }
        else if (currentScreen == Constant.LoginScreens.PILATES_TYPES) {
            if(pilatesTypesSetup.getSelectedTypes().size() == 0) {
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("Select at least one Pilates Type.");
                return false;
            }
            return true;
        }
        else if (currentScreen == Constant.LoginScreens.CONFIRMATION)
            return true;
        else if (currentScreen == Constant.LoginScreens.COMPLETE)
            return true;
        else if (currentScreen == Constant.LoginScreens.TRAINEE_EXPERIENCE_LEVEL) {
            if (traineeSignupInfo.getExperienceLevel() == null) {
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("Select experience level.");
                return false;
            }

            return true;
        } else if (currentScreen == Constant.LoginScreens.TRAINEE_GOALS) {
            if (traineeSignupInfo.getGoals().size() == 0) {
                tvSignupErrorMessage.setVisibility(View.VISIBLE);
                tvSignupErrorMessage.setText("Select your goals.");
                return false;
            }

            return true;
        }
        else if(currentScreen == Constant.LoginScreens.TRAINEE_INSTRUCTOR)
            return true;
        else if(currentScreen == Constant.LoginScreens.TRAINEE_COMPLETE)
            return true;

        tvSignupErrorMessage.setVisibility(View.VISIBLE);
        tvSignupErrorMessage.setText("Invalid data");
        return false;
    }


    private LoginActivity getActivity() {
        return this;
    }
}
