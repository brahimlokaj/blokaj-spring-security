package org.blokaj.services;

import org.blokaj.models.requests.LoginUser;
import org.blokaj.models.requests.RefreshToken;
import org.blokaj.models.responses.MyProfile;
import org.blokaj.models.responses.Response;
import org.blokaj.models.responses.ResponseToken;

public interface AuthenticationService {

    Response<ResponseToken> login(LoginUser loginUser);

    Response<ResponseToken> refreshToken(RefreshToken refreshToken);

    Response<MyProfile> getMyProfile();

}
