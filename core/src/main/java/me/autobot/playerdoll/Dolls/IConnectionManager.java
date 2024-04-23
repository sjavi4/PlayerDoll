package me.autobot.playerdoll.Dolls;

import java.util.ArrayList;
import java.util.List;

public interface IConnectionManager {
    List<IConnectionManager> instances = new ArrayList<>();

    Thread getThread();
}
