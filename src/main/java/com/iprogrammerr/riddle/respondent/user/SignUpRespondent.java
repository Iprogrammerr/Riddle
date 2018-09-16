package com.iprogrammerr.riddle.respondent.user;

import com.iprogrammerr.bright.server.request.MatchedRequest;
import com.iprogrammerr.bright.server.respondent.Respondent;
import com.iprogrammerr.bright.server.response.BadRequestResponse;
import com.iprogrammerr.bright.server.response.CreatedResponse;
import com.iprogrammerr.bright.server.response.Response;
import com.iprogrammerr.riddle.email.EmailServer;
import com.iprogrammerr.riddle.security.Encryption;
import com.iprogrammerr.riddle.user.ToSignUpJsonUser;
import com.iprogrammerr.riddle.user.ToValidateSignUpUser;
import com.iprogrammerr.riddle.user.ValidatableToSignUpUser;
import com.iprogrammerr.riddle.users.Users;

public class SignUpRespondent implements Respondent {

    private final String activatingLinkBase;
    private final Users users;
    private final EmailServer emailServer;
    private final Encryption encryption;

    public SignUpRespondent(String activatingLinkBase, Users users, EmailServer emailServer, Encryption encryption) {
	this.activatingLinkBase = activatingLinkBase;
	this.users = users;
	this.emailServer = emailServer;
	this.encryption = encryption;
    }

    @Override
    public Response respond(MatchedRequest request) throws Exception {
	try {
	    ValidatableToSignUpUser toSignUpUser = new ToValidateSignUpUser(
		    new ToSignUpJsonUser(new String(request.body())));
	    toSignUpUser.validate();
	    if (users.exists(toSignUpUser.name())) {
		return new BadRequestResponse(toSignUpUser.name() + " is taken already");
	    }
	    if (users.exists(toSignUpUser.email())) {
		return new BadRequestResponse(toSignUpUser.email() + " is taken already");
	    }
	    String hashedPassword = encryption.hash(toSignUpUser.password());
	    long id = users.createPlayer(toSignUpUser.name(), toSignUpUser.email(), hashedPassword);
	    String userHash = encryption.hash(toSignUpUser.name());
	    String activatingLink = activatingLinkBase + "?id=" + id + "&activate=" + userHash;
	    emailServer.sendSigningUp(toSignUpUser.email(), activatingLink);
	    return new CreatedResponse("Account has been created. Check your email make it active.");
	} catch (Exception exception) {
	    exception.printStackTrace();
	    return new BadRequestResponse(exception.getMessage());
	}
    }

}
