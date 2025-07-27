package com.eoral.loanapi.service.impl;

import com.eoral.loanapi.entity.Customer;
import com.eoral.loanapi.exception.BadRequestException;
import com.eoral.loanapi.exception.ForbiddenException;
import com.eoral.loanapi.exception.NotFoundException;
import com.eoral.loanapi.repository.CustomerRepository;
import com.eoral.loanapi.service.EntityDtoConversionService;
import com.eoral.loanapi.service.SecurityContextService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultCustomerServiceTest {

    @Mock
    private EntityDtoConversionService entityDtoConversionService;
    @Mock
    private SecurityContextService securityContextService;
    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private DefaultCustomerService defaultCustomerService;

    @Test
    public void checkCustomerShouldThrowExceptionWhenCustomerIdIsNull() {
        Long customerId = null;
        RuntimeException exception = assertThrows(BadRequestException.class,
                () -> defaultCustomerService.checkCustomer(customerId));
        assertEquals("Customer is not specified.", exception.getMessage());
    }

    @Test
    public void checkCustomerShouldThrowExceptionWhenCustomerIsNotFound() {
        Long customerId = 1L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(NotFoundException.class,
                () -> defaultCustomerService.checkCustomer(customerId));
        assertEquals("Customer is not found.", exception.getMessage());
    }

    @Test
    public void checkCustomerShouldReturnCustomer() {
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(customerId);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        Customer returnedCustomer = defaultCustomerService.checkCustomer(customerId);
        assertEquals(customer, returnedCustomer);
    }

    @Test
    public void checkCustomerCanBeManagedByCurrentUserShouldNotThrowExceptionWhenCurrentUserHasAdminRole() {
        String user = "john";
        Customer customer = new Customer();
        customer.setCreatedBy(user);
        when(securityContextService.currentUserHasAdminRole()).thenReturn(true);
        defaultCustomerService.checkCustomerCanBeManagedByCurrentUser(customer);
    }

    @Test
    public void checkCustomerCanBeManagedByCurrentUserShouldNotThrowExceptionWhenCurrentUserDoesNotHaveAdminRoleButCustomerWasCreatedByCurrentUser() {
        String user = "john";
        Customer customer = new Customer();
        customer.setCreatedBy(user);
        when(securityContextService.currentUserHasAdminRole()).thenReturn(false);
        when(securityContextService.getCurrentUsername()).thenReturn(user);
        defaultCustomerService.checkCustomerCanBeManagedByCurrentUser(customer);
    }

    @Test
    public void checkCustomerCanBeManagedByCurrentUserShouldThrowExceptionWhenCurrentUserDoesNotHaveAdminRoleAndCustomerWasNotCreatedByCurrentUser() {
        String user = "john";
        String anotherUser = "michael";
        Customer customer = new Customer();
        customer.setCreatedBy(user);
        when(securityContextService.currentUserHasAdminRole()).thenReturn(false);
        when(securityContextService.getCurrentUsername()).thenReturn(anotherUser);
        RuntimeException exception = assertThrows(ForbiddenException.class,
                () -> defaultCustomerService.checkCustomerCanBeManagedByCurrentUser(customer));
        assertEquals("Customer cannot be managed by current user.", exception.getMessage());
    }
}
