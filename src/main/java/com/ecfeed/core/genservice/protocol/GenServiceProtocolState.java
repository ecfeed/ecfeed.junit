package com.ecfeed.core.genservice.protocol;

public enum GenServiceProtocolState {

    AFTER_INITIALIZE,
    AFTER_BEG_DATA,
    AFTER_BEG_CHUNK,
    AFTER_END_CHUNK,
    AFTER_END_DATA
}
