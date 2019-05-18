package com.semonsys.server.controller;

final class PathHolder {
    private PathHolder() { }

    static final String SERVER_PATH = "/rest/secured/server";
    static final String SERVER_ACTIVATION_PATH = "/activate";
    static final String SERVER_ACTIVATED_PATH = "/activated";
    static final String SERVER_DELETE_PATH = "/delete";

    static final String AUTH_PATH = "/rest";
    static final String LOGOUT_PATH = "/secured/logout";
    static final String REFRESH_TOKENS_PATH = "/secured/refresh-tokens";
    static final String LOGIN_PATH = "/login";

    static final String COMPOSITE_DATA_PATH = "/rest/secured/data/comp";
    static final String COMPOSITE_DATA_IDENTIFIERS_PATH = "/identifiers";
    static final String COMPOSITE_DATA_LAST_PATH = "/last";
    static final String COMPOSITE_DATA_SERIES_PATH = "/series";

    static final String DATA_GROUP_PATH = "/rest/secured/group";

    static final String DATA_TYPE_PATH = "/rest/secured/data_type";

    static final String REGISTRATION_PATH = "/rest";
    static final String REGISTRATION_ENDPOINT_PATH = "/registration";
    static final String ACTIVATE_ACCOUNT_PATH = "/secured/activate";
    static final String CONFIRM_REGISTRATION_PATH = "/confirm";

    static final String SINGLE_DATA_PATH = "/rest/secured/data/sing";
    static final String SINGLE_DATA_LAST_PATH = "/last";
    static final String SINGLE_DATA_SERIES_PATH = "/series";

}
