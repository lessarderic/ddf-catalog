/**
 * Copyright (c) Codice Foundation
 *
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 *
 **/
package ddf.catalog.event.retrievestatus;

import ddf.catalog.operation.ResourceRequest;
import ddf.catalog.operation.ResourceResponse;
import ddf.catalog.resource.Resource;
import org.junit.BeforeClass;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;

import java.io.Serializable;
import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DownloadsStatusEventPublisherTest {

    private static EventAdmin eventAdmin;
    private static ResourceResponse resourceResponse;
    private static ResourceRequest resourceRequest;
    private static Resource resource;
    private static Map<String, Serializable> properties;
    private static DownloadsStatusEventPublisher publisher;

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DownloadsStatusEventPublisherTest.class);

    @BeforeClass
    public static void oneTimeSetup() {
        resourceResponse = mock(ResourceResponse.class);
        resourceRequest = mock(ResourceRequest.class);
        resource = mock(Resource.class);
        properties = mock(Map.class);

        when(resource.getName()).thenReturn("testCometDSessionID");
        when(properties.get(DownloadsStatusEventPublisher.USER)).thenReturn("testUser");
        when(resourceRequest.getProperties()).thenReturn(properties);
        when(resourceResponse.getResource()).thenReturn(resource);
        when(resourceResponse.getRequest()).thenReturn(resourceRequest);
    }

    @org.junit.Test
    public void testPostRetrievalStatusHappyPath() {
        setupPublisher();

        publisher.postRetrievalStatus(resourceResponse, DownloadsStatusEventPublisher.PRODUCT_RETRIEVAL_STARTED,
                null, 0L);
        verify(eventAdmin, times(1)).postEvent(any(Event.class));

        // Test with null bytes
        publisher.postRetrievalStatus(resourceResponse,
                DownloadsStatusEventPublisher.PRODUCT_RETRIEVAL_STARTED, null, null);
        verify(eventAdmin, times(2)).postEvent(any(Event.class));

        publisher.postRetrievalStatus(resourceResponse,
                DownloadsStatusEventPublisher.PRODUCT_RETRIEVAL_CANCELLED, "test detail", 20L);
        verify(eventAdmin, times(3)).postEvent(any(Event.class));

        publisher.postRetrievalStatus(resourceResponse,
                DownloadsStatusEventPublisher.PRODUCT_RETRIEVAL_FAILED, "test detail", 250L);
        verify(eventAdmin, times(4)).postEvent(any(Event.class));

        publisher.postRetrievalStatus(resourceResponse,
                DownloadsStatusEventPublisher.PRODUCT_RETRIEVAL_RETRYING, "test detail", 350L);
        verify(eventAdmin, times(5)).postEvent(any(Event.class));

        publisher.postRetrievalStatus(resourceResponse,
                DownloadsStatusEventPublisher.PRODUCT_RETRIEVAL_COMPLETE, "test detail", 500L);
        verify(eventAdmin, times(6)).postEvent(any(Event.class));
    }

    @org.junit.Test
    public void testPostRetrievalStatusWithNoNameProperty() {
        setupPublisher();

        publisher.postRetrievalStatus(resourceResponse, DownloadsStatusEventPublisher.PRODUCT_RETRIEVAL_STARTED,
                null, 0L);
        verify(eventAdmin, times(1)).postEvent(any(Event.class));

        publisher.postRetrievalStatus(resourceResponse,
                DownloadsStatusEventPublisher.PRODUCT_RETRIEVAL_STARTED, "test detail", 10L);
        verify(eventAdmin, times(2)).postEvent(any(Event.class));

        publisher.postRetrievalStatus(resourceResponse,
                DownloadsStatusEventPublisher.PRODUCT_RETRIEVAL_CANCELLED, "test detail", 20L);
        verify(eventAdmin, times(3)).postEvent(any(Event.class));

        publisher.postRetrievalStatus(resourceResponse,
                DownloadsStatusEventPublisher.PRODUCT_RETRIEVAL_FAILED, "test detail", 250L);
        verify(eventAdmin, times(4)).postEvent(any(Event.class));

        publisher.postRetrievalStatus(resourceResponse,
                DownloadsStatusEventPublisher.PRODUCT_RETRIEVAL_RETRYING, "test detail", 350L);
        verify(eventAdmin, times(5)).postEvent(any(Event.class));

        publisher.postRetrievalStatus(resourceResponse,
                DownloadsStatusEventPublisher.PRODUCT_RETRIEVAL_COMPLETE, "test detail", 500L);
        verify(eventAdmin, times(6)).postEvent(any(Event.class));
    }

    private void setupPublisher() {
        eventAdmin = mock(EventAdmin.class);
        publisher = new DownloadsStatusEventPublisher(eventAdmin);
    }

}