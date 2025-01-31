package com.lumem.hgest.model.Util;

import com.lumem.hgest.model.Role.RoleEnum;
import com.lumem.hgest.model.StoredUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class StoredUserCreator {
    private PasswordEncoder passwordEncoder;

    public StoredUserCreator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public StoredUser createUser(String password, String userName, RoleEnum roleEnum){
        String salt = RandomString.getAlphaNumericString(20);
        String hash = passwordEncoder.encode(password + salt);
        return new StoredUser(hash,roleEnum,userName,salt);
    }
}
class RandomString {

    static String getAlphaNumericString(int n) {

        String AlphaNumericString = "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}
