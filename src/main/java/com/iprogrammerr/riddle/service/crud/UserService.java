package com.iprogrammerr.riddle.service.crud;

import com.iprogrammerr.riddle.database.QueryExecutor;
import com.iprogrammerr.riddle.database.QueryToObjectConverter;
import com.iprogrammerr.riddle.entity.User;
import com.iprogrammerr.riddle.entity.UserRole;

public class UserService {

    private static final String QUERY_USER_ROLE_PREFIX = "r";
    private QueryExecutor queryExecutor;

    public UserService(QueryExecutor queryExecutor) {
	this.queryExecutor = queryExecutor;
    }

    public long createUser(User user) {
	StringBuilder insertBuilder = new StringBuilder();
	insertBuilder.append("insert into user values(").append(user.getEmail()).append(",").append(user.getName())
		.append(",").append(user.getUserRole().getId()).append(",").append(user.isActive() ? "1" : "0")
		.append(")");
	return queryExecutor.executeCreate(insertBuilder.toString());
    }

    public void activateUser(long id) {
	String update = "update user set active = 1 where id = " + id;
	queryExecutor.executeUpdate(update);
    }

    public User getUser(long id) {
	return queryExecutor.executeSelect(getUserByIdQuery(id), getQueryToUserConverter());
    }

    public User getUserByName(String name) {
	return queryExecutor.executeSelect(getUserByNameQuery(name), getQueryToUserConverter());
    }

    public User getUserByEmail(String email) {
	return queryExecutor.executeSelect(getUserByEmailQuery(email), getQueryToUserConverter());
    }

    public User getUserByNameOrEmail(String nameOrEmail) {
	if (nameOrEmail.contains("@")) {
	    return getUserByEmail(nameOrEmail);
	}
	return getUserByName(nameOrEmail);
    }

    public UserRole getUserRoleByName(String name) {
	String select = "select * from user_role where name = " + name;
	return queryExecutor.executeSelect(select, resultSet -> {
	    return new UserRole(resultSet.getLong("id"), resultSet.getString("name"));
	});
    }

    public boolean existsByEmail(String email) {
	String select = "select id from user where email = " + email;
	return queryExecutor.executeSelect(select, resultSet -> {
	    return resultSet.getLong("id") > 0;
	});
    }

    public boolean existsByName(String name) {
	String select = "select id from user where name = " + name;
	return queryExecutor.executeSelect(select, resultSet -> {
	    return resultSet.getLong("id") > 0;
	});
    }

    private QueryToObjectConverter<User> getQueryToUserConverter() {
	return (resultSet) -> {
	    UserRole userRole = new UserRole(resultSet.getLong(QUERY_USER_ROLE_PREFIX + "id"),
		    resultSet.getString(QUERY_USER_ROLE_PREFIX + "name"));
	    return new User(resultSet.getLong("id"), resultSet.getString("email"), resultSet.getString("name"),
		    resultSet.getString("password"), resultSet.getString("image_path"), resultSet.getBoolean("active"),
		    userRole);
	};
    }

    private StringBuilder getUserWithoutWhereQuery() {
	StringBuilder queryBuilder = new StringBuilder();
	queryBuilder.append("select user.*, user_role.id as ").append(QUERY_USER_ROLE_PREFIX).append("id,")
		.append("user_role.name as ").append(QUERY_USER_ROLE_PREFIX).append("name ")
		.append("from user inner join user_role on user_role.id = user.user_role_id ");
	return queryBuilder;
    }

    private String getUserByIdQuery(long id) {
	return getUserWithoutWhereQuery().append("where user.id = ").append(String.valueOf(id)).toString();
    }

    private String getUserByNameQuery(String name) {
	return getUserWithoutWhereQuery().append("where user.name = ").append(name).toString();
    }

    private String getUserByEmailQuery(String email) {
	return getUserWithoutWhereQuery().append("where user.email = ").append(email).toString();
    }

}
