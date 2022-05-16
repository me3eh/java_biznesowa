package wipb.jsfdemo.web.controller;

import wipb.jsfdemo.web.service.PrimeNumberService;

import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigInteger;

@Named
@ViewScoped
public class PrimeNumberController implements Serializable {

    @EJB
    private PrimeNumberService primesService;

    public PrimeNumberController() {
        System.out.println("PrimesController "+this);
    }

    private BigInteger number;
    private Boolean prime = Boolean.FALSE;

    public BigInteger getNumber() {
        return number;
    }

    public void setNumber(BigInteger number) {
        this.number = number;
    }

    public Boolean getPrime() {
        return prime;
    }

    public void onCheck() {

        prime = primesService.primeCheck(number);
    }

}
