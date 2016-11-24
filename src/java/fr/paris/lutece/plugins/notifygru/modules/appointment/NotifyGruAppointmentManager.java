/*
 * Copyright (c) 2002-2016, Mairie de Paris
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
import fr.paris.lutece.plugins.modulenotifygrumappingmanager.business.NotifygruMappingManager;
import fr.paris.lutece.plugins.modulenotifygrumappingmanager.business.NotifygruMappingManagerHome;
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
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
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
    /** The _list provider notify gru manager. */
    private static Map<String, NotifyGruAppointmentManager> _listProviderNotifyGruManager;

    /** The _str key. */
    private static String _strKey = "notifygru-appointment.ProviderService.@.";

    /** The _bean demand type service. */
    private static IDemandTypeService _beanDemandTypeService;

    /** The Constant BEAN_SERVICE_DEMAND_TYPE. */
    private static final String BEAN_SERVICE_DEMAND_TYPE = "notifygru-appointment.DefaultDemandTypeService";

    /** The Constant MARK_LIST_RESPONSE. */
    // MARKS   
    private static final String MARK_LIST_RESPONSE = "listEntry";

    /** The Constant MARK_FIRSTNAME. */
    private static final String MARK_FIRSTNAME = "firstName";

    /** The Constant MARK_LASTNAME. */
    private static final String MARK_LASTNAME = "lastName";

    /** The Constant MARK_DATE_APOINTMENT. */
    private static final String MARK_DATE_APOINTMENT = "date_appointment";

    /** The Constant MARK_TIME_APOINTMENT. */
    private static final String MARK_TIME_APOINTMENT = "time_appointment";

    /** The Constant MARK_REFERENCE. */
    private static final String MARK_REFERENCE = "reference";

    /** The Constant MARK_RECAP. */
    private static final String MARK_RECAP = "recap";

    /** The Constant MARK_URL_CANCEL. */
    private static final String MARK_URL_CANCEL = "url_cancel";

    /** The Constant MARK_APPOINTMENT. */
    private static final String MARK_APPOINTMENT = "appointment";

    /** The Constant MARK_SLOT. */
    private static final String MARK_SLOT = "slot";

    /** The Constant MARK_EMAIL. */
    private static final String MARK_EMAIL = "email";

    /** The Constant MARK_ENTRY_BASE. */
    private static final String MARK_ENTRY_BASE = "reponse_";

    /** The Constant TITLE_I18NKEY. */
    private static final String TITLE_I18NKEY = "module.notifygru.appointment.module.providerappointment";

    /** The Constant TEMPLATE_INFOS_HELP. */
    private static final String TEMPLATE_INFOS_HELP = "admin/plugins/workflow/modules/notifygru/appointment/freemarker_list.html";

    /** The Constant TEMPLATE_INFOS_RECAP. */
    private static final String TEMPLATE_INFOS_RECAP = "admin/plugins/workflow/modules/notifygru/appointment/recap.html";

    /** The _resource history service. */
    // SERVICES
    @Inject
    private IResourceHistoryService _resourceHistoryService;

    /** The _action dao. */
    @Inject
    private ActionDAO _actionDAO;

    /** The _nid form appointment. */
    private int _nidFormAppointment;

    /** The _n order phone number. */
    private int _nOrderMobilePhoneNumber;

    /** The _appointment gru. */
    private AppointmentGru _appointmentGru;

    /** The _appointment. */
    private Appointment _appointment;

    /** The _resource history. */
    private ResourceHistory _resourceHistory;

    /** The _appointment form. */
    private AppointmentForm _appointmentForm;

    /**
     * Gets the appointment.
     *
     * @param nIdResourceHistory the n id resource history
     * @return the appointment
     */
    private Appointment getAppointment( int nIdResourceHistory )
    {
        if ( ( _appointment == null ) ||
                ( ( _resourceHistory != null ) && ( nIdResourceHistory != _resourceHistory.getId(  ) ) ) )
        {
            _appointment = AppointmentHome.findByPrimaryKey( getResourceHistory( nIdResourceHistory ).getIdResource(  ) );
        }

        if ( _appointment == null )
        {
            throw new AppException( "Appointment is NULL" );
        }

        return _appointment;
    }

    //  AppointmentForm appointmentForm = AppointmentFormHome.findByPrimaryKey( getIdFormAppointment(  ) );

    /**
     * Gets the appointment form.
     *
     * @return the appointment form
     */
    private AppointmentForm getAppointmentForm(  )
    {
        if ( _appointmentForm == null )
        {
            _appointmentForm = AppointmentFormHome.findByPrimaryKey( getIdFormAppointment(  ) );
        }

        if ( _appointmentForm == null )
        {
            throw new AppException( "_appointmentForm is NULL" );
        }

        return _appointmentForm;
    }

    /**
     * Gets the appointment gru.
     *
     * @param nIdResourceHistory the n id resource history
     * @return the appointment gru
     */
    private AppointmentGru getAppointmentGru( int nIdResourceHistory )
    {
        if ( ( _appointmentGru == null ) ||
                ( ( _resourceHistory != null ) && ( nIdResourceHistory != _resourceHistory.getId(  ) ) ) )
        {
            _appointmentGru = AppointmentGruService.getService(  )
                                                   .getAppointmentGru( getAppointment( nIdResourceHistory ),
                    getBeanName(  ) );
        }

        if ( _appointmentGru == null )
        {
            throw new AppException( "AppointmentGRU is NULL" );
        }

        return _appointmentGru;
    }

    /**
     * Gets the resource history.
     *
     * @param nIdResourceHistory the n id resource history
     * @return the resource history
     */
    private ResourceHistory getResourceHistory( int nIdResourceHistory )
    {
        if ( ( _resourceHistory == null ) ||
                ( ( _resourceHistory != null ) && ( nIdResourceHistory != _resourceHistory.getId(  ) ) ) )
        {
            _resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        }

        if ( _resourceHistory == null )
        {
            throw new AppException( "Ressource History is NULL" );
        }

        return _resourceHistory;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getUserEmail(int)
     */
    @Override
    public String getUserEmail( int nIdResourceHistory )
    {
        return getAppointment( nIdResourceHistory ).getEmail(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getUserGuid(int)
     */
    @Override
    public String getUserGuid( int nIdResourceHistory )
    {
        return getAppointmentGru( nIdResourceHistory ).getGuid(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getOptionalMobilePhoneNumber(int)
     */
    @Override
    public String getOptionalMobilePhoneNumber( int nIdResourceHistory )
    {
        return getAppointmentGru( nIdResourceHistory ).getMobilePhoneNumber(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getInfosHelp(java.util.Locale)
     */
    @Override
    public String getInfosHelp( Locale local )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdResource( getAppointmentForm(  ).getIdForm(  ) );
        entryFilter.setResourceType( AppointmentForm.RESOURCE_TYPE );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        List<Entry> listEntry = EntryHome.getEntryList( entryFilter );

        model.put( MARK_LIST_RESPONSE, listEntry );

        @SuppressWarnings( "deprecation" )
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
            AppointmentSlot appointmentSlot = AppointmentSlotHome.findByPrimaryKey( getAppointment( nIdResourceHistory ).getIdSlot(  ) );

            model.put( MARK_FIRSTNAME, getAppointment( nIdResourceHistory ).getFirstName(  ) );
            model.put( MARK_LASTNAME, getAppointment( nIdResourceHistory ).getLastName(  ) );
            model.put( MARK_EMAIL, getAppointment( nIdResourceHistory ).getEmail(  ) );
            model.put( MARK_DATE_APOINTMENT, getAppointment( nIdResourceHistory ).getDateAppointment(  ) );

            int nMinute = appointmentSlot.getStartingMinute(  );
            String strPrefixMinute = "";

            if ( nMinute < 10 )
            {
                strPrefixMinute = "0";
            }

            model.put( MARK_TIME_APOINTMENT,
                appointmentSlot.getStartingHour(  ) + "h:" + strPrefixMinute + nMinute + "mn" );

            model.put( MARK_REFERENCE,
                getAppointmentForm(  ).getReference(  ) + '-' +
                getAppointment( nIdResourceHistory ).getIdAppointment(  ) );

            String strUrlCancel = "/";
            strUrlCancel = AppointmentApp.getCancelAppointmentUrl( getAppointment( nIdResourceHistory ) );

            model.put( MARK_URL_CANCEL, strUrlCancel.replaceAll( "&", "&amp;" ) );

            Map<String, Object> modelRecap = new HashMap<String, Object>(  );
            modelRecap.put( MARK_APPOINTMENT, getAppointment( nIdResourceHistory ) );
            modelRecap.put( MARK_SLOT, appointmentSlot );

            @SuppressWarnings( "deprecation" )
            HtmlTemplate t = AppTemplateService.getTemplateFromStringFtl( AppTemplateService.getTemplate( 
                        TEMPLATE_INFOS_RECAP, Locale.getDefault(  ), modelRecap ).getHtml(  ), Locale.getDefault(  ),
                    modelRecap );

            String strRecap = t.getHtml(  );

            model.put( MARK_RECAP, strRecap );

            //  appointment.
            List<Response> listResponses = AppointmentHome.findListResponse( getAppointment( nIdResourceHistory )
                                                                                 .getIdAppointment(  ) );

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

            EntryFilter entryFilter = new EntryFilter(  );
            entryFilter.setIdResource( getAppointmentForm(  ).getIdForm(  ) );
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
        return getAppointment( nIdResourceHistory ).getIdAppointment(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getOptionalDemandIdType(int)
     */
    @Override
    public int getOptionalDemandIdType( int nIdResourceHistory )
    {
        if ( _beanDemandTypeService == null )
        {
            _beanDemandTypeService = SpringContextService.getBean( BEAN_SERVICE_DEMAND_TYPE );
        }

        int nDemandType = _beanDemandTypeService.getDemandType( getAppointmentForm(  ) );
        AppLogService.info( "DemandTypeId : " + nDemandType );

        return nDemandType;
    }

    /**
     * Gets the order phone number.
     *
     * @return the order phone number
     */
    public int getOrderMobilePhoneNumber(  )
    {
        return _nOrderMobilePhoneNumber;
    }

    /**
     * Sets the order phone number.
     *
     * @param nOrderPhoneNumber the new order phone number
     */
    public void setOrderMobilePhoneNumber( int nOrderPhoneNumber )
    {
        _nOrderMobilePhoneNumber = nOrderPhoneNumber;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getDemandReference(int)
     */
    @Override
    public String getDemandReference( int nIdResourceHistory )
    {
        return getAppointmentForm(  ).getReference(  ) + '-' +
        getAppointment( nIdResourceHistory ).getIdAppointment(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getCustomerId(int)
     */
    @Override
    public String getCustomerId( int nIdResourceHistory )
    {
        return  getAppointmentGru( nIdResourceHistory ).getCuid(  ) ;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.AbstractServiceProvider#updateListProvider(fr.paris.lutece.plugins.workflowcore.service.task.ITask)
     */
    @Override
    public void updateListProvider( ITask task )
    {
        List<AppointmentForm> listAppointmentForms = AppointmentFormHome.getAppointmentFormsList(  );

        if ( _listProviderNotifyGruManager == null )
        {
            _listProviderNotifyGruManager = new HashMap<String, NotifyGruAppointmentManager>(  );
        }

        for ( AppointmentForm appointmentForm : listAppointmentForms )
        {
            String strKeyProvider = _strKey + appointmentForm.getIdForm(  );
            String strBeanName = _strKey + appointmentForm.getIdForm(  );

            Action action = _actionDAO.load( task.getAction(  ).getId(  ) );

            Workflow wf = action.getWorkflow(  );

            if ( !_listProviderNotifyGruManager.containsKey( strKeyProvider ) && appointmentForm.getIsActive(  ) &&
                    ( wf.getId(  ) == appointmentForm.getIdWorkflow(  ) ) )
            {
                NotifyGruAppointmentManager provider = new NotifyGruAppointmentManager(  );
                provider._resourceHistoryService = _resourceHistoryService;
                provider._actionDAO = _actionDAO;

                provider.setBeanName( strBeanName );
                provider.setKey( strKeyProvider );
                provider.setTitleI18nKey( TITLE_I18NKEY );
                provider.setIdFormAppointment( appointmentForm.getIdForm(  ) );
                provider.setManagerProvider( true );

                NotifygruMappingManager mapping = NotifygruMappingManagerHome.findByPrimaryKey( strBeanName );

                if ( mapping != null )
                {
                    provider.setOrderMobilePhoneNumber( mapping.getMobilePhoneNumber(  ) );
                }

                _listProviderNotifyGruManager.put( strKeyProvider, provider );
            }

            //
        }
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.AbstractServiceProvider#updateListProvider()
     */
    @Override
    public void updateListProvider(  )
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

            if ( !_listProviderNotifyGruManager.containsKey( strKeyProvider ) )
            {
                NotifyGruAppointmentManager provider = new NotifyGruAppointmentManager(  );
                provider._resourceHistoryService = _resourceHistoryService;
                provider._actionDAO = _actionDAO;

                provider.setBeanName( strBeanName );
                provider.setKey( strKeyProvider );
                provider.setTitleI18nKey( TITLE_I18NKEY );
                provider.setIdFormAppointment( appointment.getIdForm(  ) );
                provider.setManagerProvider( true );

                NotifygruMappingManager mapping = NotifygruMappingManagerHome.findByPrimaryKey( strBeanName );

                if ( mapping != null )
                {
                    provider.setOrderMobilePhoneNumber( mapping.getMobilePhoneNumber(  ) );
                }

                _listProviderNotifyGruManager.put( strKeyProvider, provider );
            }

            //
        }
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.AbstractServiceProvider#buildReferenteListProvider()
     */
    @Override
    public ReferenceList buildReferenteListProvider(  )
    {
        ReferenceList referenceList = new ReferenceList(  );

        for ( Map.Entry<String, NotifyGruAppointmentManager> entrySet : _listProviderNotifyGruManager.entrySet(  ) )
        {
            NotifyGruAppointmentManager provider = entrySet.getValue(  );

            //must be select. we have in the provider selection
            AppointmentForm appointmentForm = AppointmentFormHome.findByPrimaryKey( provider.getIdFormAppointment(  ) );

            referenceList.addItem( provider.getBeanName(  ),
                provider.getTitle( Locale.getDefault(  ) ) + " : " + appointmentForm.getTitle(  ) );
        }

        return referenceList;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.AbstractServiceProvider#isKeyProvider(java.lang.String)
     */
    @Override
    public Boolean isKeyProvider( String strKey )
    {
        return ( _listProviderNotifyGruManager == null ) ? false : _listProviderNotifyGruManager.containsKey( strKey );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.AbstractServiceProvider#getInstanceProvider(java.lang.String)
     */
    @Override
    public AbstractServiceProvider getInstanceProvider( String strKey )
    {
        return _listProviderNotifyGruManager.get( strKey );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.AbstractServiceProvider#getReferenteListEntityProvider()
     */
    @Override
    public ReferenceList getReferenteListEntityProvider(  )
    {
        ReferenceList referenceList = new ReferenceList(  );
        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdResource( getAppointmentForm(  ).getIdForm(  ) );
        entryFilter.setResourceType( AppointmentForm.RESOURCE_TYPE );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        List<Entry> listEntry = EntryHome.getEntryList( entryFilter );

        for ( Entry entry : listEntry )
        {
        	referenceList.addItem( entry.getPosition(  ), entry.getTitle(  ) );
        }

        return referenceList;
    }
}
