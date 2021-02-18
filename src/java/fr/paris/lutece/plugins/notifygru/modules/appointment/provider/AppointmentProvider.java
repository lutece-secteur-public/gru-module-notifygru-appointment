/*
 * Copyright (c) 2002-2018, Mairie de Paris
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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.Strings;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.service.AppointmentResponseService;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.web.AppointmentApp;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.appointmentgru.business.AppointmentGru;
import fr.paris.lutece.plugins.appointmentgru.services.AppointmentGruService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflow.modules.comment.business.CommentValue;
import fr.paris.lutece.plugins.workflow.modules.comment.service.CommentValueService;
import fr.paris.lutece.plugins.workflow.modules.comment.service.ICommentValueService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.provider.IProvider;
import fr.paris.lutece.plugins.workflowcore.service.provider.InfoMarker;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.TaskService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.l10n.LocaleService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 *
 */
public class AppointmentProvider implements IProvider
{
    // PROPERTY KEY
    private static final String PROPERTY_SMS_SENDER_NAME = "workflow-notifygruappointment.gruprovider.sms.sendername";

    // INFO MESSAGE KEY
    private static final String MESSAGE_MARKER_FIRSTNAME = "module.notifygru.appointment.task_notify_appointment_config.label_firstname";
    private static final String MESSAGE_MARKER_LASTNAME = "module.notifygru.appointment.task_notify_appointment_config.label_lastname";
    private static final String MESSAGE_MARKER_EMAIL = "module.notifygru.appointment.task_notify_appointment_config.label_email";
    private static final String MESSAGE_MARKER_DATE_APOINTMENT = "module.notifygru.appointment.task_notify_appointment_config.label_date_appointment";
    private static final String MESSAGE_MARKER_TIME_APOINTMENT = "module.notifygru.appointment.task_notify_appointment_config.label_time_appointment";
    private static final String MESSAGE_MARKER_REFERENCE = "module.notifygru.appointment.task_notify_appointment_config.label_reference";
    private static final String MESSAGE_MARKER_URL_CANCEL = "module.notifygru.appointment.task_notify_appointment_config.label_url_cancel";
    private static final String MESSAGE_MARKER_RECAP = "module.notifygru.appointment.task_notify_appointment_config.label_recap";
    private static final String MESSAGE_MARKER_CANCEL_MOTIVE = "module.notifygru.appointment.task_notify_appointment_config.label_motif_cancel";
    private static final String MESSAGE_URL_APPOINTMENT_DASHBOARD = "module.notifygru.appointment.task_notify_appointment_config.label_url_appointment_dashboard";
    private static final String MESSAGE_MARKER_URL_REPORT = "module.notifygru.appointment.task_notify_appointment_config.label_url_report";

    // INFOS RECAP
    private static final String INFOS_RECAP_MARK_APPOINTMENT = "appointment";
    private static final String TEMPLATE_INFOS_RECAP = "admin/plugins/workflow/modules/notifygru/appointment/recap.html";
    // PROPERTIES
    private static final String PROPERTIE_DATE_FORMAT = AppPropertiesService.getProperty( "notifygru.appointment.dateformat", "dd-MM-yyyy" );

    /**
     * The name of the XPage
     */
    private static final String XPAGE_NAME = "appointment";
    
    // NEEDED OBJECTS
    /** The _appointment. */
    private AppointmentDTO _appointment;
    /** The _appointment form. */
    private AppointmentFormDTO _appointmentForm;
    /** The _appointment gru. */
    private AppointmentGru _appointmentGru;

    // SERVICES
    private IResourceHistoryService _resourceHistoryService = SpringContextService.getBean( ResourceHistoryService.BEAN_SERVICE );
    private ITaskService _taskService = SpringContextService.getBean( TaskService.BEAN_SERVICE );
    private ICommentValueService _commentService = SpringContextService.getBean( CommentValueService.BEAN_SERVICE );

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
        _appointment = AppointmentService.buildAppointmentDTOFromIdAppointment( resourceHistory.getIdResource( ) );
        if ( _appointment == null )
        {
            throw new AppException( "No appointment for resource history Id : " + resourceHistory.getIdResource( ) );
        }
        _appointmentForm = FormService.buildAppointmentForm( Integer.parseInt( strAppointmentFormId ), 0 );
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
    public String provideDemandSubtypeId( )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String provideDemandReference( )
    {
        String strReference = StringUtils.isEmpty( _appointmentForm.getReference( ) ) ? ""
                : ( Strings.toUpperCase( _appointmentForm.getReference( ).trim( ) ) + " - " );
        strReference += _appointment.getReference( );

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
    public String provideSmsSender( )
    {
        return AppPropertiesService.getProperty( PROPERTY_SMS_SENDER_NAME );
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
    public Collection<InfoMarker> provideMarkerValues( )
    {
        Collection<InfoMarker> collectionNotifyMarkers = new ArrayList<>( );

        // GENERIC
        collectionNotifyMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_FIRSTNAME, _appointment.getFirstName( ) ) );
        collectionNotifyMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_LASTNAME, _appointment.getLastName( ) ) );
        collectionNotifyMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_EMAIL, _appointment.getEmail( ) ) );
        collectionNotifyMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_REFERENCE, provideDemandReference( ) ) );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern( PROPERTIE_DATE_FORMAT );
        Slot slot =  _appointment.getSlot().get( 0 );
        collectionNotifyMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_DATE_APOINTMENT, formatter.format( slot.getDate( ) ) ) );
        collectionNotifyMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_TIME_APOINTMENT, _appointment.getStartingTime( ).toString( ) ) );
        String strUrlCancel = AppointmentApp.getCancelAppointmentUrl( _appointment );
        String strUrlReport = AppointmentApp.getReportAppointmentUrl( _appointment );
        collectionNotifyMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_URL_CANCEL, strUrlCancel.replaceAll( "&", "&amp;" ) ) );
        collectionNotifyMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_URL_REPORT, strUrlReport.replaceAll( "&", "&amp;" ) ) );
        collectionNotifyMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_URL_APPOINTMENT_DASHBOARD, getDashboardAppointmentUrl( _appointment ).replaceAll( "&", "&amp;" ) ) );
        collectionNotifyMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_CANCEL_MOTIVE, getCommentValue( ) ) );
        Map<String, Object> modelRecap = new HashMap< >( );
        modelRecap.put( INFOS_RECAP_MARK_APPOINTMENT, _appointment );
        @SuppressWarnings( "deprecation" )
        HtmlTemplate t = AppTemplateService.getTemplateFromStringFtl( AppTemplateService.getTemplate( TEMPLATE_INFOS_RECAP, Locale.getDefault( ), modelRecap )
                .getHtml( ), Locale.getDefault( ), modelRecap );
        collectionNotifyMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_RECAP, t.getHtml( ) ) );

        // ENTRIES
        List<Response> listResponses = AppointmentResponseService.findListResponse( _appointment.getIdAppointment( ) );
        for ( Response response : listResponses )
        {
            Entry entry = EntryHome.findByPrimaryKey( response.getEntry( ).getIdEntry( ) );
            collectionNotifyMarkers.add( createMarkerValues( AppointmentNotifyGruConstants.MARK_ENTRY_BASE + entry.getPosition( ),
                    response.getResponseValue( ) ) );
        }

        return collectionNotifyMarkers;
    }

    /**
     * Get the comment value (motiv of the cancellation of the appointment)
     * 
     * @return
     */
    private String getCommentValue( )
    {    	
        String strCommentValue = StringUtils.EMPTY;
        // Get the id of the appointment
        int nIdResource = _appointment.getIdAppointment( );     
        // Get the form
        Form form = FormService.findFormLightByPrimaryKey( _appointment.getIdForm( ) );
        // Get all the resource of this appointment and this workflow
        List<ResourceHistory> listResourceHistory = _resourceHistoryService.getAllHistoryByResource( nIdResource, Appointment.APPOINTMENT_RESOURCE_TYPE,
                form.getIdWorkflow( ) );        
        // For each resource
        for ( ResourceHistory resourceHistory : listResourceHistory )
        {
            // Get the id history of the resource
            int nIdHistory = resourceHistory.getId( );
            // Get the id of the action
            int nIdAction = resourceHistory.getAction( ).getId( );
            // Get all the tasks of this action
            List<ITask> listTasks = _taskService.getListTaskByIdAction( nIdAction, LocaleService.getDefault( ) );
            // For each task
            for ( ITask task : listTasks )
            {
                // Get the id of the task
                int nIdTask = task.getId( );
                // Try to find a comment for this task and this id history
                CommentValue commentValue = _commentService.findByPrimaryKey( nIdHistory, nIdTask, WorkflowUtils.getPlugin( ) );
                if ( commentValue != null )
                {
                    strCommentValue = commentValue.getValue( );
                    break;
                }
            }
            if ( StringUtils.isNotEmpty( strCommentValue ) )
            {
                break;
            }
        }
        return strCommentValue;
    }

    /**
     * static method for retrieving descriptions of available marks for a given appointmentForm
     * 
     * @param nAppointmentFormId
     *            id of the appointmentForm
     * @return Collection of InfoMarker
     */
    public static Collection<InfoMarker> getProviderMarkerDescriptions( int nAppointmentFormId )
    {
        Collection<InfoMarker> collectionNotifyMarkers = new ArrayList<>( );

        // GENERIC
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_FIRSTNAME, MESSAGE_MARKER_FIRSTNAME, null ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_LASTNAME, MESSAGE_MARKER_LASTNAME, null ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_EMAIL, MESSAGE_MARKER_EMAIL, null ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_DATE_APOINTMENT, MESSAGE_MARKER_DATE_APOINTMENT, null ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_TIME_APOINTMENT, MESSAGE_MARKER_TIME_APOINTMENT, null ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_REFERENCE, MESSAGE_MARKER_REFERENCE, null ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_URL_CANCEL, MESSAGE_MARKER_URL_CANCEL, null ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_URL_REPORT, MESSAGE_MARKER_URL_REPORT, null ) );
        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_URL_APPOINTMENT_DASHBOARD, MESSAGE_URL_APPOINTMENT_DASHBOARD, null ) );

        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_RECAP, MESSAGE_MARKER_RECAP, null ) );

        // ENTRIES
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( nAppointmentFormId );
        entryFilter.setResourceType( AppointmentFormDTO.RESOURCE_TYPE );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        List<Entry> listEntry = EntryHome.getEntryList( entryFilter );
        for ( Entry entry : listEntry )
        {
            if ( !StringUtils.isEmpty( entry.getTitle( ) ) )
            {
                collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_ENTRY_BASE + entry.getPosition( ), null,
                        entry.getTitle( ) ) );
            }
        }

        collectionNotifyMarkers.add( createMarkerDescriptions( AppointmentNotifyGruConstants.MARK_CANCEL_MOTIVE, MESSAGE_MARKER_CANCEL_MOTIVE, null ) );

        return collectionNotifyMarkers;
    }

    /**
     * Construct a InfoMarker with value for given parameters
     * 
     * @param strMarker
     * @param strValue
     * @return a InfoMarker
     */
    private static InfoMarker createMarkerValues( String strMarker, String strValue )
    {
        InfoMarker notifyMarker = new InfoMarker( strMarker );
        notifyMarker.setValue( strValue );

        return notifyMarker;
    }

    /**
     * Construct a InfoMarker with descrition for given parameters
     * 
     * @param strMarker
     * @param strDescription
     * @return a InfoMarker
     */
    private static InfoMarker createMarkerDescriptions( String strMarker, String strDescriptionI18n, String strDescription )
    {
        InfoMarker notifyMarker = new InfoMarker( strMarker );
        if ( strDescriptionI18n != null )
        {
            notifyMarker.setDescription( I18nService.getLocalizedString( strDescriptionI18n, I18nService.getDefaultLocale( ) ) );
        }
        else
        {
            notifyMarker.setDescription( strDescription );
        }

        return notifyMarker;
    }
    /**
     * Get the URL user dashboard 
     * 
     * @param appointment
     *            The appointment
     * @return The URL user dashboard  appointment
     */
    private static String getDashboardAppointmentUrl( Appointment appointment )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getProdUrl( StringUtils.EMPTY ) + AppPathService.getPortalUrl( ) );
        urlItem.addParameter( MVCUtils.PARAMETER_PAGE, XPAGE_NAME );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, "getMyAppointments" );
        return urlItem.getUrl( );
    }


}
