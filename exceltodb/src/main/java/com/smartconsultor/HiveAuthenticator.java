package com.smartconsultor;

import java.util.Hashtable;
import javax.security.sasl.AuthenticationException;
import org.apache.hive.service.auth.PasswdAuthenticationProvider;

/*
 javac -cp hive-service-4.0.0.jar HiveAuthenticator.java -d .
 jar cf HiveAuthenticator.jar com/smartconsultor
 cp HiveAuthenticator.jar $HIVE_HOME/lib/.
*/

public class HiveAuthenticator implements PasswdAuthenticationProvider {

    Hashtable<String, String> store = null;
  
    public HiveAuthenticator () {
      store = new Hashtable<String, String>();
      store.put("user1", "passwd1");
      store.put("user2", "passwd2");
    }
  
    @Override
    public void Authenticate(String user, String  password)
        throws AuthenticationException {
  
      String storedPasswd = store.get(user);
  
      if (storedPasswd != null && storedPasswd.equals(password))
        return;
       
      throw new AuthenticationException("SampleAuthenticator: Error validating user");
    }  
}