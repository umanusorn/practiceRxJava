package com.umitems.app.practicerxjava;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;
import com.umitems.app.practicerxjava.databinding.ActivityMainBinding;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

       setupRxBinding();
       //setUpCommonObservable();
    }

    private void setupRxBinding() {
        //I use com.jakewharton.rxbinding2:rxbinding:2.0.0 to help connect with the ui
        RxTextView.textChangeEvents(binding.editText)
            .debounce(1000, TimeUnit.MILLISECONDS, Schedulers.computation())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(getObserver());
    }

    private void setUpCommonObservable() {
        // I wonder how to emit string to the observer onTextChanged without create a new Observable
        //The clue might be in the class TextViewTextChangeEventObservable
        //todo make this work
        final Observer<String> observer = getStringObserver();
        final Observable<String> observable = getStringObservable();
        observable
            .debounce(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer);

        binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //observer.onNext(String.valueOf(s));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @NonNull
    private Observer<String> getStringObserver() {
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String value) {
                MainActivity.this.onNext(value);
            }

            @Override
            public void onError(Throwable e) {
                MainActivity.this.onError(e);
            }

            @Override
            public void onComplete() {
                MainActivity.this.onComplete();
            }
        };
    }

    private Observable<String> getStringObservable() {
        return Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                }
            });
    }

    private Observer<TextViewTextChangeEvent> getObserver() {
        return new Observer<TextViewTextChangeEvent>() {

            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(TextViewTextChangeEvent textChangeEvent) {
                String value = textChangeEvent.text().toString();
                MainActivity.this.onNext(value);

            }

            @Override
            public void onError(Throwable e) {
                MainActivity.this.onError(e);
            }

            @Override
            public void onComplete() {
                MainActivity.this.onComplete();

            }
        };
    }

    private void onComplete() {
        binding.textView.append(" onComplete");
        binding.textView.append(AppConstant.LINE_SEPARATOR);
    }

    private void onError(Throwable e) {
        binding.textView.append(" onError : " + e.getMessage());
        binding.textView.append(AppConstant.LINE_SEPARATOR);
    }

    private void onNext(String value) {
        if(value.length()>0){
            binding.textView.append(" Search : " + value);
            binding.textView.append(AppConstant.LINE_SEPARATOR);
        }
    }
}
