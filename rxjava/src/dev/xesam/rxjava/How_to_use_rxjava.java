package dev.xesam.rxjava;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by xe on 15-4-25.
 */
public class How_to_use_rxjava {
    public static void main(String[] args) {
        Observable<String> observable = Observable.from(new String[]{"a", "b"});
        observable.subscribe(new Subscriber<String>() {
            @Override
            public void onNext(String s) {
                System.out.println("1 onNext: " + s);
            }

            @Override
            public void onCompleted() {
                System.out.println("1 onCompleted ");
            }

            @Override
            public void onError(Throwable e) {
            }
        });
        observable.subscribe(new Subscriber<String>() {
            @Override
            public void onNext(String s) {
                System.out.println("2 onNext: " + s);
            }

            @Override
            public void onCompleted() {
                System.out.println("2 onCompleted ");
            }

            @Override
            public void onError(Throwable e) {
            }
        });
    }
}
