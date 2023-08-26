package com.banquito.core.productsaccounts.service;

import com.banquito.core.productsaccounts.exception.CRUDException;
import com.banquito.core.productsaccounts.model.ProductAccount;
import com.banquito.core.productsaccounts.repository.ProductAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductAccountServiceTest {

    @Mock
    private ProductAccountRepository productAccountRepository;

    @InjectMocks
    private ProductAccountService productAccountService;

    private ProductAccount productAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productAccount = new ProductAccount();
        productAccount.setId("1");
        productAccount.setName("Savings");
        productAccount.setDescription("Savings Account");
        productAccount.setMinimunBalance(new BigDecimal(1000));
        productAccount.setPayInterest("Y");
        productAccount.setAcceptsChecks("N");
        productAccount.setState("ACT");
        productAccount.setCreationDate(new Date());
    }

    @Test
    void listAllActives() {
        List<ProductAccount> productAccounts = List.of(productAccount);
        when(productAccountRepository.findByState("ACT")).thenReturn(productAccounts);

        List<ProductAccount> resultProductAccount = productAccountService.listAllActives();

        assertEquals(productAccounts, resultProductAccount);
        verify(productAccountRepository, times(1)).findByState("ACT");
    }

    @Test
    void obtainByIdCRUDException() {
        when(productAccountRepository.findById(productAccount.getId())).thenReturn(Optional.empty());
        assertThrows(CRUDException.class, () -> {
            productAccountService.obtainById(productAccount.getId());
        });
        verify(productAccountRepository, never()).save(any(ProductAccount.class));
        verify(productAccountRepository, times(1)).findById(productAccount.getId());
    }

    @Test
    void create() {
        when(productAccountRepository.save(productAccount)).thenReturn(productAccount);
        assertDoesNotThrow(() -> productAccountService.create(productAccount));
        verify(productAccountRepository, times(1)).save(productAccount);
    }
}