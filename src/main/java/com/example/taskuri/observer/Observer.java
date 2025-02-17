package com.example.taskuri.observer;

public interface Observer<T> {
    void onUpdate(T eventData);
}
