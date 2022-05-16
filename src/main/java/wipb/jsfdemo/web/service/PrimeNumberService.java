package wipb.jsfdemo.web.service;

import wipb.jsfdemo.web.interceptor.InterceptorOfServices;

import javax.ejb.*;
import javax.interceptor.Interceptors;
import java.math.BigInteger;

@Stateless
@Interceptors({InterceptorOfServices.class})
public class PrimeNumberService {

    @EJB
    private PrimeNumberCacheService primeNumberCacheService;

    public boolean primeCheck(BigInteger number) {

        if(primeNumberCacheService.primeSaved(number)){
            return primeNumberCacheService.isPrime(number);
        }
        if (!number.isProbablePrime(5))
            return returnValueProxy(false, number);

        BigInteger two = new BigInteger("2");
        if (two.equals(number) || BigInteger.ZERO.equals(number.mod(two)))
            return returnValueProxy(false, number);

        for (BigInteger i = new BigInteger("3"); i.multiply(i).compareTo(number) < 1; i = i.add(two)) {
            if (BigInteger.ZERO.equals(number.mod(i)))
                return returnValueProxy(false, number);
        }
        return returnValueProxy(true, number);
    }
    private boolean returnValueProxy(boolean returningValue, BigInteger number){
        primeNumberCacheService.addPrimeNumber(number, returningValue);
        return returningValue;
    }
}

