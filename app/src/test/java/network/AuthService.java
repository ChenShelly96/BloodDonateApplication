package network;

import com.google.gson.JsonElement;
import com.androidapp.blooddonate.UserAuthRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("identitytoolkit/v3/relyingparty/signupNewUser")
    Call<JsonElement> registerUserEmail(@Body UserAuthRequest payload);

    @POST("identitytoolkit/v3/relyingparty/verifyPassword")
    Call<JsonElement> loginUser(@Body UserAuthRequest payload);
}
