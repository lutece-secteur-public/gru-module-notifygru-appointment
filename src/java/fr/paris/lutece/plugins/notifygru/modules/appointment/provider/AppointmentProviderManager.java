/*
 * Copyright (c) 2002-2022, City of Paris
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
import java.util.List;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.modulenotifygrumappingmanager.service.IProviderManagerWithMapping;
import fr.paris.lutece.plugins.workflow.service.provider.ProviderManagerUtil;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.provider.IProvider;
import fr.paris.lutece.plugins.workflowcore.service.provider.ProviderDescription;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.util.ReferenceList;

/**
 *
 */
@ApplicationScoped
@Named( AppointmentProviderManager.BEAN_SERVICE )
public class AppointmentProviderManager implements IProviderManagerWithMapping
{
    public static final String BEAN_SERVICE = "notifygru-appointment.ProviderService";

    private String _strId;

    private static final String MESSAGE_PROVIDER_LABEL = "module.notifygru.appointment.module.providerappointment";

    @Inject
    private ActionService _actionService;

    /**
     * Default constructor for CDI proxy
     */
    public AppointmentProviderManager( )
    {
        this( BEAN_SERVICE );
    }

    /**
     * @param strId
     */
    public AppointmentProviderManager( String strId )
    {
        _strId = strId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId( )
    {
        return _strId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ProviderDescription> getAllProviderDescriptions( ITask task )
    {
        Collection<ProviderDescription> collectionProviderDescriptions = new ArrayList<>( );

        List<AppointmentFormDTO> listAppointmentForm = FormService.buildAllActiveAppointmentForm( );
        for ( AppointmentFormDTO appointmentForm : listAppointmentForm )
        {
            Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );
            Workflow wf = action.getWorkflow( );

            if ( ( wf.getId( ) == appointmentForm.getIdWorkflow( ) ) )
            {
                collectionProviderDescriptions.add( getProviderDescription( String.valueOf( appointmentForm.getIdForm( ) ) ) );
            }
        }

        return collectionProviderDescriptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ProviderDescription> getAllProviderDescriptions( )
    {
        Collection<ProviderDescription> collectionProviderDescriptions = new ArrayList<>( );

        List<AppointmentFormDTO> listAppointmentForm = FormService.buildAllActiveAppointmentForm( );
        for ( AppointmentFormDTO appointmentForm : listAppointmentForm )
        {
            collectionProviderDescriptions.add( getProviderDescription( String.valueOf( appointmentForm.getIdForm( ) ) ) );
        }

        return collectionProviderDescriptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProviderDescription getProviderDescription( String strProviderId )
    {
        AppointmentFormDTO appointmentForm = FormService.buildAppointmentFormLight( Integer.parseInt( strProviderId ) );
        if ( !appointmentForm.getIsActive( ) )
        {
            return null;
        }
        ProviderDescription providerDescription = new ProviderDescription( String.valueOf( appointmentForm.getIdForm( ) ),
                I18nService.getLocalizedString( MESSAGE_PROVIDER_LABEL, I18nService.getDefaultLocale( ) ) + appointmentForm.getIdForm( ) );
        providerDescription.setMarkerDescriptions( AppointmentProvider.getProviderMarkerDescriptions( appointmentForm.getIdForm( ) ) );

        return providerDescription;
    }

    /**
     * Creates the provider of an appointment resource.
     *
     * @param strProviderId
     *            the id of the appointment form
     * @param resourceHistory
     *            the resource history the task runs on
     * @param request
     *            the request
     * @return the provider, or null when the resource is not an existing appointment
     */
    @Override
    public IProvider createProvider( String strProviderId, ResourceHistory resourceHistory, HttpServletRequest request )
    {
        if ( resourceHistory == null || !Appointment.APPOINTMENT_RESOURCE_TYPE.equals( resourceHistory.getResourceType( ) )
                || AppointmentService.findAppointmentById( resourceHistory.getIdResource( ) ) == null )
        {
            return null;
        }
        return new AppointmentProvider( ProviderManagerUtil.buildCompleteProviderId( getId( ), strProviderId ), strProviderId, resourceHistory );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getMappingPropertiesForProvider( String strProviderId )
    {
        ReferenceList referenceList = new ReferenceList( );

        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( Integer.parseInt( strProviderId ) );
        entryFilter.setResourceType( AppointmentFormDTO.RESOURCE_TYPE );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        List<Entry> listEntry = EntryHome.getEntryList( entryFilter );
        for ( Entry entry : listEntry )
        {
            if ( entry.getTitle( ) != null )
            {
                referenceList.addItem( entry.getPosition( ), entry.getTitle( ) );
            }
        }
        return referenceList;
    }
}
