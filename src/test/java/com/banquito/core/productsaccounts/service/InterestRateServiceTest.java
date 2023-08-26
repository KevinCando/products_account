package com.banquito.core.productsaccounts.service;

import com.banquito.core.productsaccounts.exception.CRUDException;
import com.banquito.core.productsaccounts.model.InterestRate;
import com.banquito.core.productsaccounts.repository.InterestRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InterestRateServiceTest {

    @Mock
    private InterestRateRepository interestRateRepository;

    @InjectMocks
    private InterestRateService interestRateService;

    private InterestRate interestRate;
    private InterestRate interestRate2;

    private List<InterestRate> interestRateList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        interestRate = new InterestRate();
        interestRate.setId(1);
        interestRate.setName("rate");
        interestRate.setInterestRate(new BigDecimal(21));
        interestRate.setState("ACT");
        interestRate.setStart(new Date());


        interestRate2 = new InterestRate();
        interestRate2.setId(2);
        interestRate2.setName("rate");
        interestRate2.setInterestRate(new BigDecimal(21));
        interestRate2.setState("ACT");
        interestRate2.setStart(new Date());

        interestRateList = new ArrayList<>();
        interestRateList.add(interestRate);
        interestRateList.add(interestRate2);

    }

    @Test
    void listAllActives() {
        when(interestRateRepository.findByState("ACT")).thenReturn(interestRateList);

        List<InterestRate> ratesResult = interestRateService.listAllActives();
        assertEquals(interestRateList, ratesResult);

        verify(interestRateRepository, times(1)).findByState("ACT");
    }

    @Test
    void obtainById() {
        when(interestRateRepository.findById(interestRate.getId())).thenReturn(Optional.empty());
        assertThrows(CRUDException.class, () -> {
            interestRateService.obtainById(interestRate.getId());
        });
        verify(interestRateRepository,never()).save(any(InterestRate.class));
        verify(interestRateRepository, times(1)).findById(interestRate.getId());
    }

    @Test
    void create() {
        InterestRateRepository customMock = mock(InterestRateRepository.class);
        when(customMock.save(interestRate)).thenThrow(new CRUDException(510, "Exception"));

        InterestRateService interestRateServiceWithCustomMock = new InterestRateService(customMock);
        assertThrows(CRUDException.class, () -> interestRateServiceWithCustomMock.create(interestRate));

        verify(customMock, times(1)).save(interestRate);
    }

    @Test
    void update() {
        when(interestRateRepository.findById(interestRate.getId())).thenReturn(Optional.of(interestRate));
        InterestRate updatedInterestRate = new InterestRate();
        updatedInterestRate.setName("updatedRate");
        updatedInterestRate.setInterestRate(new BigDecimal(25));

        interestRateService.update(interestRate.getId(), updatedInterestRate);

        verify(interestRateRepository, times(1)).findById(interestRate.getId());
        verify(interestRateRepository, times(1)).save(interestRate);

        assertEquals(updatedInterestRate.getName(), interestRate.getName());
        assertEquals(updatedInterestRate.getInterestRate(), interestRate.getInterestRate());
    }

    @Test
    void inactivateNotFound() {
        when(interestRateRepository.findById(interestRate.getId())).thenReturn(Optional.empty());

        assertThrows(CRUDException.class, () -> interestRateService.inactivate(interestRate.getId()));
        verify(interestRateRepository, times(1)).findById(interestRate.getId());
    }

    @Test
    void inactivateWithError() {

        when(interestRateRepository.findById(interestRate.getId())).thenReturn(Optional.of(interestRate2));
        doThrow(new RuntimeException("Some error")).when(interestRateRepository).save(interestRate2);

        assertThrows(CRUDException.class, () -> interestRateService.inactivate(interestRate.getId()));
    }
}