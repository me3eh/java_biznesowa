package wipb.jsfdemo.web.service;

import wipb.jsfdemo.web.interceptor.InterceptorOfServices;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.interceptor.Interceptors;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Singleton
@Interceptors({InterceptorOfServices.class})
public class PrimeNumberCacheService {

    @PostConstruct
    private void init() {
    }
    Map<BigInteger, Boolean> primeNumbers = new HashMap<>();
    Map<BigInteger, Boolean> hashMapForDeletingPrimeNumbers = new HashMap<>();

    public void addPrimeNumber(BigInteger number, Boolean isPrime){
        if(!primeNumbers.containsKey(number)){
            System.out.println("Dodano liczbe " + number);
            primeNumbers.put(number, isPrime);
            hashMapForDeletingPrimeNumbers.put(number, true);
        }
    }
    public Boolean primeSaved(BigInteger number){
        Boolean lol = primeNumbers.containsKey(number);
        System.out.println(lol);
        return lol;
    }

    public Boolean isPrime(BigInteger number){
        if(primeNumbers.containsKey(number)){
            System.out.println("Było");
            hashMapForDeletingPrimeNumbers.put(number, true);
            return primeNumbers.get(number);
        }
        System.out.println("Nigdy sie nie wyswietli");
        return false;
    }
    @Schedule(hour = "*", minute = "*/2", persistent = false)
    @Asynchronous
    public void clearRedundantPrimeNumbers(){
        System.out.println("Lmao");
        Map<BigInteger, Boolean> cloneOfHashMap = new HashMap<>(hashMapForDeletingPrimeNumbers);
        for( BigInteger numbers: cloneOfHashMap.keySet()){
            if( !cloneOfHashMap.get(numbers) ) {
                hashMapForDeletingPrimeNumbers.remove(numbers);
                System.out.println("Do usuniecia: " + numbers);
            }
            else {
                System.out.println("Na false: " + numbers);
                hashMapForDeletingPrimeNumbers.put(numbers, false);
            }
        }
    }
}
