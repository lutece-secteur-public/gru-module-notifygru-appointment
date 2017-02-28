/*
 * Copyright (c) 2002-2017, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.notifygru.modules.appointment.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.Strings;

import fr.paris.lutece.plugins.appointment.business.Appointment;
import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.plugins.appointment.business.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlotHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.web.AppointmentApp;
import fr.paris.lutece.plugins.appointmentgru.business.AppointmentGru;
import fr.paris.lutece.plugins.appointmentgru.services.AppointmentGruService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.IProvider;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.NotifyGruMarker;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 *
 */
public class AppointmentProvider implements IProvider
{
    // INFO MESSAGE KEY
    private static final String MESSAGE_MARKER_FIRSTNAME = "module.notifygru.appointment.task_notify_appointment_config.label_firstname";
    private static final String MESSAGE_MARKER_LASTNAME = "module.notifygru.appointment.task_notify_appointment_config.label_lastname";
    private static final String MESSAGE_MARKER_EMAIL = "module.notifygru.appointment.task_notify_appointment_config.label_email";
    private static final String MESSAGE_MARKER_DATE_APOINTMENT = "module.notifygru.appointment.task_notify_appointment_config.label_date_appointment";
    private static final String MESSAGE_MARKER_TIME_APOINTMENT = "module.notifygru.appointment.task_notify_appointment_config.label_time_appointment";
    private static final String MESSAGE_MARKER_REFERENCE = "module.notifygru.appointment.task_notify_appointment_config.label_reference";
    private static final String MESSAGE_MARKER_URL_CANCEL = "module.notifygru.appointment.task_notify_appointment_config.label_url_cancel";
    private static final String MESSAGE_MARKER_RECAP = "module.notifygru.appointment.task_notify_appointment_config.label_recap";

    // INFOS RECAP
    private static final String INFOS_RECAP_MARK_APPOINTMENT = "appointment";
    private static final String INFOS_RECAP_MARK_SLOT = "slot";
    private static final String TEMPLATE_INFOS_RECAP = "admin/plugins/workflow/modules/notifygru/appointment/recap.html";

    // NEEDED OBJECTS
    /** The _appointment. */
    private Appointment _appointment;
    /** The _appointment form. */
    private AppointmentForm _appointmentForm;
    /** The _appointment gru. */
    private AppointmentGru _appointmentGru;

    /**
     * Constructor
     * 
     * @param beanProviderName
     *            , id of the provider bean (with providerManager id) for appointmentGru search
     * @param strAppointmentFormId
     *            , if of the appointmentForm
     * @param resourceHistory
     *            , the resource wich require the provider
     */
    public AppointmentProvider( String beanProviderName, String strAppointmentFormId, ResourceHistory resourceHistory )
    {
        super( );
        _appointment = AppointmentHome.findByPrimaryKey( resourceHistory.getIdResource( ) );
        if ( _appointment == null )
        {
            throw new AppException( "No appointment for resource history Id : " + resourceHistory.getIdResource( ) );
        }
        _appointmentForm = AppointmentFormHome.findByPrimaryKey( Integer.parseInt( strAppointmentFormId ) );
        if ( _appointmentForm == null )
        {
            throw new AppException( "No appointmentForm for  Id : " + strAppointmentFormId );
        }
        _appointmentGru = AppointmentGruService.getService( ).getAppointmentGru( _appointment, beanProviderName );
        if ( _appointment == null )
        {
            throw new AppException( "No appointmentGru for appointment : " + _appointment.getIdAppointment( ) + " and beanProvider : " + beanProviderName );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String provideDemandId( )
    {
        return String.valueOf( _appointment.getIdAppointment( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String provideDemandTypeId( )
    {
        return String.valueOf( _appointmentGru.getDemandeTypeId( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String provideDemandReference( )
    {
        String strReference = StringUtils.isEmpty( _appointmentForm.getReference( ) ) ? ""
                : ( Strings.toUpperCase( _appointmentForm.getReference( ).trim( ) ) + " - " );
        strReference += AppointmentService.getService( ).computeRefAppointment( _appointment );

        return strReference;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String provideCustomerConnectionId( )
    {
        return _appointmentGru.getGuid( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String provideCustomerId( )
    {
        return _appointmentGru.getCuid( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String provideCustomerEmail( )
    {
        return _appointment.getEmail( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String provideCustomerMobilePhone( )
    {
        return _appointmentGru.getMobilePhoneNumber( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<NotifyGruMarker> provideMarkerValues( )
    {
        Collection<NotifyGruMarker> collectionNotifyGruMarkers = new ArrayList<>( );

        // GENERIC
        collectionNotifyGruMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_FIRSTNAME, _appointment.getFirstName( ) ) );
        collectionNotifyGruMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_LASTNAME, _appointment.getLastName( ) ) );
        collectionNotifyGruMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_EMAIL, _appointment.getEmail( ) ) );
        collectionNotifyGruMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_REFERENCE, provideDemandReference( ) ) );
        // FIXME date envoyee avec un toString, ne faudrait-il pas un DateFormat en properties ?
        collectionNotifyGruMarkers
                .add( createMarkerValues( AppointmentNotifyGruConstants.MARK_DATE_APOINTMENT, _appointment.getDateAppointment( ).toString( ) ) );

        AppointmentSlot appointmentSlot = AppointmentSlotHome.findByPrimaryKey( _appointment.getIdSlot( ) );
        collectionNotifyGruMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_TIME_APOINTMENT,
                String.format( "%1$dh:%2$02dmn", appointmentSlot.getStartingHour( ), appointmentSlot.getStartingMinute( ) ) ) );

        String strUrlCancel = AppointmentApp.getCancelAppointmentUrl( _appointment );
        collectionNotifyGruMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_URL_CANCEL, strUrlCancel.replaceAll( "&", "&amp;" ) ) );

        Map<String, Object> modelRecap = new HashMap<String, Object>( );
        modelRecap.put( INFOS_RECAP_MARK_APPOINTMENT, _appointment );
        modelRecap.put( INFOS_RECAP_MARK_SLOT, appointmentSlot );
        @SuppressWarnings( "deprecation" )
        HtmlTemplate t = AppTemplateService.getTemplateFromStringFtl( AppTemplateService.getTemplate( TEMPLATE_INFOS_RECAP, Locale.getDefault( ), modelRecap )
                .getHtml( ), Locale.getDefault( ), modelRecap );
        collectionNotifyGruMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_RECAP, t.getHtml( ) ) );

        // ENTRIES
        List<Response> listResponses = AppointmentHome.findListResponse( _appointment.getIdAppointment( ) );
        for ( Response response : listResponses )
        {
            Entry entry = EntryHome.findByPrimaryKey( response.getEntry( ).getIdEntry( ) );
            collectionNotifyGruMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_ENTRY_BASE + entry.getPosition( ),
                    response.getResponseValue( ) ) );
        }

        return collectionNotifyGruMarkers;
    }

    /**
     * static method for retrieving descriptions of available marks for a given appointmentForm
     * 
     * @param nAppointmentFormId
     *            id of the appointmentForm
     * @return Collection of NotifyGruMarker
     */
    public static Collection<NotifyGruMarker> getProviderMarkerDescriptions( int nAppointmentFormId )
    {
        Collection<NotifyGruMarker> collectionNotifyGruMarkers = new ArrayList<>( );

        // GENERIC
        collectionNotifyGruMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_FIRSTNAME, MESSAGE_MARKER_FIRSTNAME, null ) );
        collectionNotifyGruMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_LASTNAME, MESSAGE_MARKER_LASTNAME, null ) );
        collectionNotifyGruMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_EMAIL, MESSAGE_MARKER_EMAIL, null ) );
        collectionNotifyGruMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_DATE_APOINTMENT, MESSAGE_MARKER_DATE_APOINTMENT, null ) );
        collectionNotifyGruMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_TIME_APOINTMENT, MESSAGE_MARKER_TIME_APOINTMENT, null ) );
        collectionNotifyGruMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_REFERENCE, MESSAGE_MARKER_REFERENCE, null ) );
        collectionNotifyGruMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_URL_CANCEL, MESSAGE_MARKER_URL_CANCEL, null ) );
        collectionNotifyGruMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_RECAP, MESSAGE_MARKER_RECAP, null ) );

        // ENTRIES
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( nAppointmentFormId );
        entryFilter.setResourceType( AppointmentForm.RESOURCE_TYPE );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        List<Entry> listEntry = EntryHome.getEntryList( entryFilter );
        for ( Entry entry : listEntry )
        {
            collectionNotifyGruMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_ENTRY_BASE + entry.getPosition( ), null,
                    entry.getTitle( ) ) );
        }

        return collectionNotifyGruMarkers;
    }

    /**
     * Construct a NotifyGruMarker with value for given parameters
     * 
     * @param strMarker
     * @param strValue
     * @return a NotifyGruMarker
     */
    private static NotifyGruMarker createMarkerValues( String strMarker, String strValue )
    {
        NotifyGruMarker notifyGruMarker = new NotifyGruMarker( strMarker );
        notifyGruMarker.setValue( strValue );

        return notifyGruMarker;
    }

    /**
     * Construct a NotifyGruMarker with descrition for given parameters
     * 
     * @param strMarker
     * @param strDescription
     * @return a NotifyGruMarker
     */
    private static NotifyGruMarker createMarkerDescriptions( String strMarker, String strDescriptionI18n, String strDescription )
    {
        NotifyGruMarker notifyGruMarker = new NotifyGruMarker( strMarker );
        if ( strDescriptionI18n != null )
        {
            notifyGruMarker.setDescription( I18nService.getLocalizedString( strDescriptionI18n, I18nService.getDefaultLocale( ) ) );
        }
        else
        {
            notifyGruMarker.setDescription( strDescription );
        }

        return notifyGruMarker;
    }
}
