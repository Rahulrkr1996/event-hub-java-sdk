package com.ge.predix.eventhub.configuration;/*
* Copyright (c) 2016 GE. All Rights Reserved.
* GE Confidential: Restricted Internal Distribution
*/

import com.ge.predix.eventhub.EventHubClientException;
import com.ge.predix.eventhub.EventHubUtils;
import com.ge.predix.eventhub.client.Client;

import java.util.ArrayList;
import java.util.List;

public class SubscribeConfiguration {
    private String subscriberName;
    private String subscriberInstance;
    private int batchSize;
    private int batchInterval;
    private BatchType batchType;
    private boolean batchingEnabled;
    private boolean acksEnabled;
    private boolean metricsEnabled;
    private int metricsInterval;
    private Client.MetricsCallBack metricsCallback;

    private int maxRetries;
    private int retryInterval;
    private int durationBeforeFirstRetry;
    private SubscribeRecency subscribeRecency;

    private List<String> topics;

    public enum SubscribeRecency {
        OLDEST("Oldest"), // default behavior
        NEWEST("Newest"); //

        private final String text;

        private SubscribeRecency(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public enum SubscribeStreamType {
        STANDARD("Standard"), // default behavior
        ACK("Ack Only"), //
        BATCH("Batch"), //
        METRICS("Metrics"); //

        private final String text;

        private SubscribeStreamType(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public enum BatchType {
        UNION("Union"), // default behavior
        INTERSECTION("Intersection");

        private final String text;

        private BatchType(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }



    /**
     * Return topics subscribing to
     *
     * @return topics
     */
    public List<String> getTopics() {
        return topics;
    }

    /**
     * @return The subscriber name this client is using
     */
    public String getSubscriberName() {
        return subscriberName;
    }

    /**
     * @return whether batching api is enabled or not
     */
    public boolean isBatchingEnabled(){
        return batchingEnabled;
    }

    /**
     * @return whether metrics api is enabled or not
     */
    public boolean isMetricsEnabled(){
        return metricsEnabled;
    }

    /**
     * @return number of messages received per request
     */

    public int getBatchSize() {
        return batchSize;
    }

    /**
     * @return time between each batch of messages received
     */
    public int getBatchInterval() {
        return batchInterval;
    }


    /**
     * The interval to get metrics messages in minutes
     * @return
     */
    public int getMetricsInterval() {
        return metricsInterval;
    }

    /**
     *  returns the metrics callback registered
     * @return
     */
    public Client.MetricsCallBack getMetricsCallback(){
        return metricsCallback;
    }


    /**
     * @return Whether acks are enabled
     */
    public boolean isAcksEnabled() {
        return acksEnabled;
    }

    /**
     * @return The subscriber instance this client is using
     */
    public String getSubscriberInstance() {
        return subscriberInstance;
    }

    /**
     * @return The maximum number of retries
     */
    public int getMaxRetries() {
        return maxRetries;
    }

    /**
     * @return The interval between each retry attempt in seconds
     */
    public int getRetryInterval() {
        return retryInterval;
    }

    /**
     * @return The duration Event Hub waits before the first retry attempt for sending a message in milliseconds
     */
    public int getDurationBeforeFirstRetry() {
        return durationBeforeFirstRetry;
    }

    /**
     * @return The subscription recency for this client
     */
    public SubscribeRecency getSubscribeRecency() {
        return subscribeRecency;
    }

    /**
     * @return The subscription batch type for this client
     */
    public BatchType getBatchType() {
        return batchType;
    }

    public static class Builder {
        //Required for all types of subscriptions
        private String subscriberName = "default-subscriber-name";
        private String subscriberInstance = "default-subscriber-id";
        private SubscribeRecency subscribeRecency = SubscribeRecency.OLDEST;
        private BatchType batchType = BatchType.INTERSECTION;


        //batching
        private boolean batchingEnabled = false;
        private int batchSize = 500;
        private int batchInterval = 100;


        //metrics
        private boolean metricsEnabled = false;
        private int metricsInterval = 5 ;
        private Client.MetricsCallBack metricsCallback;

        //TODO: Error Checking for these fields
        private int maxRetries = 5;

        private boolean acksEnabled = false;
        private int retryInterval = 30; //seconds
        private int durationBeforeRetry = 30; //seconds
        private List<String> topics = new ArrayList<String>();

        /**
         * Set topics for subscription
         *
         * @param topics
         * @return
         */
        public Builder topics(List<String> topics) {
            this.topics.addAll(topics);
            return this;
        }

        /**
         * Set topic for subscription
         *
         * @param topic
         * @return
         */
        public Builder topic(String topic) {
            this.topics.add(topic);
            return this;
        }

        /**
         * Configures the subscriber name of the client. Each unique subscriber will receive all the messages
         *
         * @param subscriberName Subscriber name this client should use. Default is default-subscriber-name
         * @return Builder
         */
        public Builder subscriberName(String subscriberName) {
            this.subscriberName = subscriberName;
            return this;
        }


        /**
         * Configures whether the subscription will acknowledge received messages. If it will, then all received messages will need to be acked for.
         *
         * @param acksEnabled
         * @return
         */
        public Builder acksEnabled(boolean acksEnabled) {
            this.acksEnabled = acksEnabled;
            return this;
        }

        /**
         * Configures the number of messages received per request when subscribingInBatch.
         * @param batchSize, must be less than 1000 and messagesize must be less than 1mb
         * @return configuration builder
         */
        public Builder batchSize(int batchSize) throws EventHubClientException.InvalidConfigurationException {
            if(batchSize > 10000 || batchSize < 1)
                throw new EventHubClientException.InvalidConfigurationException("batch size must be between 1 and 10000");
            this.batchSize = batchSize;
            return this;
        }

        /**
         * Enable batching for the subscriber client
         *
         * @param batchingEnabled
         */
        public Builder batchingEnabled(boolean batchingEnabled){
            this.batchingEnabled = batchingEnabled;
            this.batchType = BatchType.INTERSECTION;
            return this;
        }


        /**
         * Configures the number of milliseconds to wait between receiving batches of messages.
         * Range between 100ms and 1000ms. Default is 100ms
         *
         * @param batchInterval
         */
        public Builder batchIntervalMilliseconds(int batchInterval) throws EventHubClientException.InvalidConfigurationException {
            if (batchInterval < 100 || batchInterval > 1000) {
                throw new EventHubClientException.InvalidConfigurationException("batch interval must be between 100ms and 1000ms");
            }
            this.batchInterval = batchInterval;
            return this;
        }


        /**
         *
         * @param metricsEnabled
         * @return
         */
        public Builder metricsEnabled(boolean metricsEnabled){
            this.metricsEnabled = metricsEnabled;
            if (this.batchingEnabled == false ){
                this.batchType = BatchType.UNION;
            }
            return this;
        }


        /**
         *  Configures the number of interval in minutes to get the metric messages.
         * Range between 1m and 60m. Default is 5m
         * @param metricsInterval
         * @return
         * @throws EventHubClientException.InvalidConfigurationException
         */
        public Builder metricsIntervalMinutes(int metricsInterval) throws EventHubClientException.InvalidConfigurationException {
            if (metricsInterval < 1 || metricsInterval > 60) {
                throw new EventHubClientException.InvalidConfigurationException("metrics interval must be between 1m and 60m");
            }
            this.metricsInterval = metricsInterval;
            return this;
        }


        /**
         *
         * @param metricsCallback
         * @return
         * @throws EventHubClientException.InvalidConfigurationException
         */
        public Builder metricsCallBack(Client.MetricsCallBack metricsCallback) throws EventHubClientException.InvalidConfigurationException {
            this.metricsCallback = metricsCallback;
            return this;
        }



        /**
         * Configures the instance of a particular subscriber name. All instances of a subscriber name will collectively receive all the messages
         *
         * @param subscriberInstance Subscriber instance this client should use. Default is default-subscriber-id
         * @return Builder
         */
        public Builder subscriberInstance(String subscriberInstance) {
            this.subscriberInstance = subscriberInstance;
            return this;
        }

        /**
         * Configure whether subscription delivers messages from oldest message available on topic for event hub instance
         * or delivers messages from next message(s) published on topic
         *
         * @param subscribeRecency Specifies OLDEST vs NEWEST. Default is OLDEST
         * @return Builder
         */
        public Builder subscribeRecency(SubscribeRecency subscribeRecency) {
            this.subscribeRecency = subscribeRecency;
            return this;
        }


        /**
         * Configures whether subscription batching ensures message batching for batch size or batch interval whichever
         * comes first OR batching enforces ceiling on batch size during the batch interval
         *
         * @param batchType Specifies Union or Intersection.Default is Intersection
         * @return Builder
         */
        public Builder batchType(BatchType batchType) {
            this.batchType = batchType;
            return this;
        }


        /**
         * retry interval indicates the time between retries when Event Hub is resending messages that have not been acked.
         *
         * @param retryInterval
         * @return
         */
        public Builder retryIntervalSeconds(int retryInterval) {
            this.retryInterval = retryInterval;
            return this;
        }

        /**
         * max retries indicates the maximum amount of times Event Hub should resend a message to a subscriber.
         *
         * @param maxRetries
         * @return
         * @throws EventHubClientException.InvalidConfigurationException
         */
        public Builder maxRetries(int maxRetries) throws EventHubClientException.InvalidConfigurationException {
            if (maxRetries >= 2 || maxRetries <= 10) {
                this.maxRetries = maxRetries;
                return this;
            } else {
                throw new EventHubClientException.InvalidConfigurationException("retry interval cannot be smaller than 2 or greater than 10");
            }
        }

        /**
         * The duration before retry is the length of time Event Hub will wait before resending a message to a subscriber.
         *
         * @param durationBeforeRetry
         * @return
         */
        public Builder durationBeforeRetrySeconds(int durationBeforeRetry) throws EventHubClientException.InvalidConfigurationException {
//      if (durationBeforeFirstRetry >= 30 || durationBeforeFirstRetry <= 60) {
            this.durationBeforeRetry = durationBeforeRetry;
            return this;
//      } else {
//        throw new EventHubClientException.InvalidConfigurationException("retry interval cannot be smaller than 2 or greater than 10");
//      }
        }

        public SubscribeConfiguration build() {
            return new SubscribeConfiguration(this);
        }
    }

    SubscribeConfiguration(Builder builder) {
        this.subscriberName = builder.subscriberName;
        this.subscriberInstance = builder.subscriberInstance;
        this.subscribeRecency = builder.subscribeRecency;
        this.durationBeforeFirstRetry = builder.durationBeforeRetry;
        this.retryInterval = builder.retryInterval;
        this.maxRetries = builder.maxRetries;
        this.acksEnabled = builder.acksEnabled;
        this.batchingEnabled = builder.batchingEnabled;
        this.batchInterval = builder.batchInterval;
        this.batchSize = builder.batchSize;
        this.batchType = builder.batchType;
        this.topics = builder.topics;
        this.metricsEnabled = builder.metricsEnabled;
        this.metricsInterval = builder.metricsInterval;
        this.metricsCallback = builder.metricsCallback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SubscribeConfiguration that = (SubscribeConfiguration) o;

        if (subscriberName != null ? !subscriberName.equals(that.subscriberName) : that.subscriberName != null)
            return false;
        return subscriberInstance != null ?
                subscriberInstance.equals(that.subscriberInstance) :
                that.subscriberInstance == null;

    }

    @Override
    public String toString() {
        ArrayList<Object> toStringValues = new ArrayList<>();
        toStringValues.add("subscriberName");
        toStringValues.add(subscriberName == null ? "null" : subscriberName);

        toStringValues.add("subscriberInstance");
        toStringValues.add(subscriberInstance == null ? "null" : subscriberInstance);
        toStringValues.add("subscribeRecency");
        toStringValues.add(subscribeRecency == null ? "null" : subscribeRecency.toString());
        toStringValues.add("acksEnabled");
        toStringValues.add(acksEnabled);
        if(this.acksEnabled){
            toStringValues.add("durationBeforeFirstRetry");
            toStringValues.add(durationBeforeFirstRetry);
            toStringValues.add("retryInterval");
            toStringValues.add(retryInterval);
            toStringValues.add("maxRetries");
            toStringValues.add(maxRetries);
        }
        toStringValues.add("batchingEnabled");
        toStringValues.add(batchingEnabled);
        if(this.batchingEnabled){
            toStringValues.add("batchSize");
            toStringValues.add(batchSize);
            toStringValues.add("batchInterval");
            toStringValues.add(batchInterval);
            toStringValues.add("batchType");
            toStringValues.add(batchType);
        }
        if(this.metricsEnabled){
            toStringValues.add("metricInterval");
            toStringValues.add(metricsInterval);
        }
        toStringValues.add("topics");
        toStringValues.add(topics == null ? ":" : topics.toString());
        return EventHubUtils.formatJson(toStringValues.toArray()).toString();
    }

    public Builder cloneConfig() throws EventHubClientException.InvalidConfigurationException {
        return new Builder()
                .acksEnabled(this.acksEnabled)
                .batchingEnabled(this.batchingEnabled)
                .batchType(this.batchType)
                .batchIntervalMilliseconds(this.batchInterval)
                .batchSize(this.batchSize)
                .durationBeforeRetrySeconds(this.durationBeforeFirstRetry)
                .maxRetries(this.maxRetries)
                .retryIntervalSeconds(this.retryInterval)
                .subscribeRecency(this.subscribeRecency)
                .subscriberInstance(this.subscriberInstance)
                .subscriberName(this.subscriberName)
                .topics(this.topics)
                .metricsEnabled(this.metricsEnabled)
                .metricsCallBack(this.metricsCallback)
                .metricsIntervalMinutes(this.metricsInterval);
    }
}
