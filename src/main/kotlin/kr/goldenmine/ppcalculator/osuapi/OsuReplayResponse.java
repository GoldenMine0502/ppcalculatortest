package kr.goldenmine.ppcalculator.osuapi;

import com.google.gson.annotations.SerializedName;

public class OsuReplayResponse {
    @SerializedName("content")
    String content;

    @SerializedName("encoding")
    String encodingType;

    public String getContent() {
        return content;
    }

    public String getEncodingType() {
        return encodingType;
    }
}
