package com.passionpenguin.ditiezu.HtmlTextView;

import android.text.Editable;

import androidx.annotation.Nullable;

import org.xml.sax.Attributes;

public interface WrapperTagHandler {
    boolean handleTag(boolean opening, String tag, Editable output, @Nullable Attributes attributes);
}
