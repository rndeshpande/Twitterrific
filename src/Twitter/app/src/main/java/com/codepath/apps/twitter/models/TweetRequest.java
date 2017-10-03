package com.codepath.apps.twitter.models;

import org.parceler.Parcel;

/**
 * Created by rdeshpan on 9/26/2017.
 */
@Parcel
public class TweetRequest {
    public String status;
    public long inReplyToStatusId;
    public String InReplyToScreenName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long id;

    public TweetRequest() {
    }

    public TweetRequest(String status) {
        this.status = status;
    }

    public TweetRequest(String status, long inReplyToStatusId, String InReplyToScreenName) {
        this.status = status;
        this.inReplyToStatusId = inReplyToStatusId;
        this.InReplyToScreenName = InReplyToScreenName;
    }

    public TweetRequest(long id, String status, long inReplyToStatusId, String InReplyToScreenName) {
        this.id = id;
        this.status = status;
        this.inReplyToStatusId = inReplyToStatusId;
        this.InReplyToScreenName = InReplyToScreenName;
    }

    public long getInReplyToStatusId() {
        return inReplyToStatusId;
    }

    public void setInReplyToStatusId(long inReplyToStatusId) {
        this.inReplyToStatusId = inReplyToStatusId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInReplyToScreenName() {
        return InReplyToScreenName;
    }

    public void setInReplyToScreenName(String inReplyToScreenName) {
        InReplyToScreenName = inReplyToScreenName;
    }
}
