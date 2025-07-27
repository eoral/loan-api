package com.eoral.loanapi.service;

public interface SecurityContextService {

    String getCurrentUsername();

    boolean currentUserHasAdminRole();

}
