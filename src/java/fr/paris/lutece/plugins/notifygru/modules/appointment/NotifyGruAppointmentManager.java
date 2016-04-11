/*
 * Copyright (c) 2002-2015, Mairie de Paris
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
package fr.paris.lutece.plugins.notifygru.modules.appointment;

import fr.paris.lutece.plugins.appointment.business.Appointment;
import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.plugins.appointment.business.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlotHome;
import fr.paris.lutece.plugins.appointment.web.AppointmentApp;
import fr.paris.lutece.plugins.appointmentgru.business.AppointmentGru;
import fr.paris.lutece.plugins.appointmentgru.services.AppointmentGruService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.notifygru.modules.appointment.services.IDemandTypeService;
import fr.paris.lutece.plugins.workflow.business.action.ActionDAO;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.AbstractServiceProvider;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;


/**
 * The Class NotifyGruAppointment.
 */
public class NotifyGruAppointmentManager extends AbstractServiceProvider
{
    private static Map<String, NotifyGruAppointmentManager> _listProviderNotifyGruManager;
    private static String _strKey = "notifygru-appointment.ProviderService.@.";
    private static IDemandTypeService _beanDemandTypeService;
    private static final String BEAN_SERVICE_DEMAND_TYPE = "notifygru-appointment.DefaultDemandTypeService";

    /** The Constant MARK_LIST_RESPONSE. */
    // MARKS   
    private static final String MARK_LIST_RESPONSE = "listEntry";

    /** The Constant MARK_FIRSTNAME. */
    private static final String MARK_FIRSTNAME = "firstName";

    /** The Constant MARK_LASTNAME. */
    private static final String MARK_LASTNAME = "lastName";
    private static final String MARK_DATE_APOINTMENT = "date_appointment";
    private static final String MARK_TIME_APOINTMENT = "time_appointment";
    private static final String MARK_REFERENCE = "reference";
    private static final String MARK_RECAP = "recap";
    private static final String MARK_URL_CANCEL = "url_cancel";
    private static final String MARK_APPOINTMENT = "appointment";
    private static final String MARK_SLOT = "slot";

    /** The Constant MARK_EMAIL. */
    private static final String MARK_EMAIL = "email";

    /** The Constant MARK_ENTRY_BASE. */
    private static final String MARK_ENTRY_BASE = "reponse_";
    private static final String TITLE_I18NKEY = "module.notifygru.appointment.module.providerappointment";
    private static final String PROPERTY_CONFIG_PROVIDER_PHONE_NUMBER = "notifygru-appointment.config.provider.PositionDemandType";

    /** The Constant TEMPLATE_INFOS_HELP. */
    private static final String TEMPLATE_INFOS_HELP = "admin/plugins/workflow/modules/notifygru/appointment/freemarker_list.html";
    private static final String TEMPLATE_INFOS_RECAP = "admin/plugins/workflow/modules/notifygru/appointment/recap.html";

    /** The _resource history service. */
    // SERVICES
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    private ActionDAO _actionDAO;

    /** The _nid form appointment. */
    private int _nidFormAppointment;

    /** The _n order phone number. */
    private int _nOrderPhoneNumber;

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getUserEmail(int)
     */
    @Override
    public String getUserEmail( int nIdResourceHistory )
    {
        String strEmail = null;
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        Appointment appointment = AppointmentHome.findByPrimaryKey( resourceHistory.getIdResource(  ) );
        strEmail = appointment.getEmail(  );

        return strEmail;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getUserGuid(int)
     */
    @Override
    public String getUserGuid( int nIdResourceHistory )
    {
        String strGUID = "";
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        Appointment appointment = AppointmentHome.findByPrimaryKey( resourceHistory.getIdResource(  ) );
        AppointmentGru appointmentGru = AppointmentGruService.getService(  ).getAppointmentGru( appointment );

        strGUID = appointmentGru.getGuid(  );

        return strGUID;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getOptionalMobilePhoneNumber(int)
     */
    @Override
    public String getOptionalMobilePhoneNumber( int nIdResourceHistory )
    {
        String strPhoneNumber = null;
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        Appointment appointment = AppointmentHome.findByPrimaryKey( resourceHistory.getIdResource(  ) );

        AppointmentGru appointmentGru = AppointmentGruService.getService(  ).getAppointmentGru( appointment );

        return appointmentGru.getMobilePhoneNumber(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getInfosHelp(java.util.Locale)
     */
    @Override
    public String getInfosHelp( Locale local )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        AppointmentForm formAppointment = AppointmentFormHome.findByPrimaryKey( _nidFormAppointment );

        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdResource( formAppointment.getIdForm(  ) );
        entryFilter.setResourceType( AppointmentForm.RESOURCE_TYPE );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        List<Entry> listEntry = EntryHome.getEntryList( entryFilter );

        model.put( MARK_LIST_RESPONSE, listEntry );

        HtmlTemplate t = AppTemplateService.getTemplateFromStringFtl( AppTemplateService.getTemplate( 
                    TEMPLATE_INFOS_HELP, local, model ).getHtml(  ), local, model );

        String strResourceInfo = t.getHtml(  );

        return strResourceInfo;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getInfos(int)
     */
    @Override
    public Map<String, Object> getInfos( int nIdResourceHistory )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        if ( nIdResourceHistory > 0 )
        {
            ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
            Appointment appointment = AppointmentHome.findByPrimaryKey( resourceHistory.getIdResource(  ) );
            AppointmentForm appointmentForm = AppointmentFormHome.findByPrimaryKey( getIdFormAppointment(  ) );

            AppointmentSlot appointmentSlot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );

            model.put( MARK_FIRSTNAME, appointment.getFirstName(  ) );
            model.put( MARK_LASTNAME, appointment.getLastName(  ) );
            model.put( MARK_EMAIL, appointment.getEmail(  ) );
            model.put( MARK_DATE_APOINTMENT, appointment.getDateAppointment(  ) );

            int nMinute = appointmentSlot.getStartingMinute(  );
            String strPrefixMinute = "";

            if ( nMinute < 10 )
            {
                strPrefixMinute = "0";
            }

            model.put( MARK_TIME_APOINTMENT,
                appointmentSlot.getStartingHour(  ) + "h:" + strPrefixMinute + nMinute + "mn" );

            model.put( MARK_REFERENCE, appointmentForm.getReference(  ) + '-' + appointment.getIdAppointment(  ) );

            String strUrlCancel = "/";
            strUrlCancel = AppointmentApp.getCancelAppointmentUrl( appointment );

            model.put( MARK_URL_CANCEL, strUrlCancel.replaceAll( "&", "&amp;" ) );

            Map<String, Object> modelRecap = new HashMap<String, Object>(  );
            modelRecap.put( MARK_APPOINTMENT, appointment );
            modelRecap.put( MARK_SLOT, appointmentSlot );

            HtmlTemplate t = AppTemplateService.getTemplateFromStringFtl( AppTemplateService.getTemplate( 
                        TEMPLATE_INFOS_RECAP, Locale.getDefault(  ), modelRecap ).getHtml(  ), Locale.getDefault(  ),
                    modelRecap );

            String strRecap = t.getHtml(  );

            model.put( MARK_RECAP, strRecap );

            //  appointment.
            List<Response> listResponses = AppointmentHome.findListResponse( appointment.getIdAppointment(  ) );

            for ( Response response : listResponses )
            { 
                Entry entry = EntryHome.findByPrimaryKey( response.getEntry(  ).getIdEntry(  ) );
                model.put( MARK_ENTRY_BASE + entry.getPosition(  ), response.getResponseValue(  ) );
            }
        }
        else
        {
            model.put( MARK_FIRSTNAME, "" );
            model.put( MARK_LASTNAME, "" );
            model.put( MARK_EMAIL, "" );

            model.put( MARK_DATE_APOINTMENT, "" );
            model.put( MARK_TIME_APOINTMENT, "" );
            model.put( MARK_REFERENCE, "" );
            model.put( MARK_URL_CANCEL, "" );
            model.put( MARK_RECAP, "" );

            AppointmentForm formAppointment = AppointmentFormHome.findByPrimaryKey( _nidFormAppointment );
            EntryFilter entryFilter = new EntryFilter(  );
            entryFilter.setIdResource( formAppointment.getIdForm(  ) );
            entryFilter.setResourceType( AppointmentForm.RESOURCE_TYPE );
            entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
            entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );

            List<Entry> listEntry = EntryHome.getEntryList( entryFilter );

            for ( Entry entry : listEntry )
            {
                model.put( MARK_ENTRY_BASE + entry.getPosition(  ), "" );
            }
        }

        return model;
    }

    /**
     * Gets the id form appointment.
     *
     * @return the id form appointment
     */
    public int getIdFormAppointment(  )
    {
        return _nidFormAppointment;
    }

    /**
     * Sets the id form appointment.
     *
     * @param nIdFormAppointment the new id form appointment
     */
    public void setIdFormAppointment( int nIdFormAppointment )
    {
        this._nidFormAppointment = nIdFormAppointment;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getOptionalDemandId(int)
     */
    @Override
    public int getOptionalDemandId( int nIdResourceHistory )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        Appointment appointment = AppointmentHome.findByPrimaryKey( resourceHistory.getIdResource(  ) );

        return appointment.getIdAppointment(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getOptionalDemandIdType(int)
     */
    @Override
    public int getOptionalDemandIdType( int nIdResourceHistory )
    {
        AppointmentForm appointmentForm = AppointmentFormHome.findByPrimaryKey( getIdFormAppointment(  ) );

        if ( _beanDemandTypeService == null )
        {
            _beanDemandTypeService = SpringContextService.getBean( BEAN_SERVICE_DEMAND_TYPE );
        }

        int nDemandType = _beanDemandTypeService.getDemandType( appointmentForm );
        AppLogService.info( "DemandTypeId : " + nDemandType );

        return nDemandType;
    }

    /**
     * Gets the appointment.
     *
     * @param nIdResourceHistory the n id resource history
     * @return the appointment
     */
    public Appointment getAppointment( int nIdResourceHistory )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        Appointment appointment = AppointmentHome.findByPrimaryKey( resourceHistory.getIdResource(  ) );

        return appointment;
    }

    /**
     * Gets the order phone number.
     *
     * @return the order phone number
     */
    public int getOrderPhoneNumber(  )
    {
        return _nOrderPhoneNumber;
    }

    /**
     * Sets the order phone number.
     *
     * @param nOrderPhoneNumber the new order phone number
     */
    public void setOrderPhoneNumber( int nOrderPhoneNumber )
    {
        _nOrderPhoneNumber = nOrderPhoneNumber;
    }

    @Override
    public String getDemandReference( int nIdResourceHistory )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        Appointment appointment = AppointmentHome.findByPrimaryKey( resourceHistory.getIdResource(  ) );
        AppointmentForm appointmentForm = AppointmentFormHome.findByPrimaryKey( getIdFormAppointment(  ) );

        return appointmentForm.getReference(  ) + '-' + appointment.getIdAppointment(  );
    }

    @Override
    public String getCustomerId( int nIdResourceHistory )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        Appointment appointment = AppointmentHome.findByPrimaryKey( resourceHistory.getIdResource(  ) );
        AppointmentGru appointmentGru = AppointmentGruService.getService(  ).getAppointmentGru( appointment );

        return String.valueOf( appointmentGru.getCuid(  ) );
    }

    @Override
    public void updateListProvider( ITask task )
    {
        List<AppointmentForm> listAppointmentForms = AppointmentFormHome.getAppointmentFormsList(  );

        if ( _listProviderNotifyGruManager == null )
        {
            _listProviderNotifyGruManager = new HashMap<String, NotifyGruAppointmentManager>(  );
        }

        for ( AppointmentForm appointment : listAppointmentForms )
        {
            String strKeyProvider = _strKey + appointment.getIdForm(  );
            String strBeanName = _strKey + appointment.getIdForm(  );

            Action action = _actionDAO.load( task.getAction(  ).getId(  ) );

            Workflow wf = action.getWorkflow(  );

            if ( !_listProviderNotifyGruManager.containsKey( strKeyProvider ) && appointment.getIsActive(  ) &&
                    ( wf.getId(  ) == appointment.getIdWorkflow(  ) ) )
            {
                NotifyGruAppointmentManager provider = new NotifyGruAppointmentManager(  );
                provider._resourceHistoryService = _resourceHistoryService;
                provider._actionDAO = _actionDAO;

                provider.setBeanName( strBeanName );
                provider.setKey( strKeyProvider );
                provider.settitleI18nKey( TITLE_I18NKEY );
                provider.setIdFormAppointment( appointment.getIdForm(  ) );

                provider.setOrderPhoneNumber( AppPropertiesService.getPropertyInt( 
                        PROPERTY_CONFIG_PROVIDER_PHONE_NUMBER, 0 ) );

                _listProviderNotifyGruManager.put( strKeyProvider, provider );
            }

            //
        }
    }

    @Override
    public ReferenceList buildReferenteListProvider(  )
    {
        ReferenceList refenreceList = new ReferenceList(  );

        for ( Map.Entry<String, NotifyGruAppointmentManager> entrySet : _listProviderNotifyGruManager.entrySet(  ) )
        {
            String key = entrySet.getKey(  );
            NotifyGruAppointmentManager provider = entrySet.getValue(  );

            // AppointmentForm directory = AppointmentFormHome.findByPrimaryKey(provider.getIdDirectory(), _pluginDirectory);
            AppointmentForm appointment = AppointmentFormHome.findByPrimaryKey( provider.getIdFormAppointment(  ) );

            refenreceList.addItem( provider.getBeanName(  ),
                provider.getTitle( Locale.getDefault(  ) ) + " : " + appointment.getTitle(  ) );
        }

        return refenreceList;
    }

    @Override
    public Boolean isKeyProvider( String strKey )
    {
        return ( _listProviderNotifyGruManager == null ) ? false : _listProviderNotifyGruManager.containsKey( strKey );
    }

    @Override
    public AbstractServiceProvider getInstanceProvider( String strKey )
    {
        return _listProviderNotifyGruManager.get( strKey );
    }
}
