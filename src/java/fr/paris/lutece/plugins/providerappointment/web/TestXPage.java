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
package fr.paris.lutece.plugins.providerappointment.web;
 
import fr.paris.lutece.plugins.providerappointment.business.Test;
import fr.paris.lutece.plugins.providerappointment.business.TestHome;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.util.url.UrlItem;

import java.util.Map;

import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;

import javax.servlet.http.HttpServletRequest; 

/**
 * This class provides the user interface to manage Test xpages ( manage, create, modify, remove )
 */
 
@Controller( xpageName = "test" , pageTitleI18nKey = "providerappointment.xpage.test.pageTitle" , pagePathI18nKey = "providerappointment.xpage.test.pagePathLabel" )
public class TestXPage extends MVCApplication
{
    // Templates
    private static final String TEMPLATE_MANAGE_TESTS="/skin/plugins/providerappointment/manage_tests.html";
    private static final String TEMPLATE_CREATE_TEST="/skin/plugins/providerappointment/create_test.html";
    private static final String TEMPLATE_MODIFY_TEST="/skin/plugins/providerappointment/modify_test.html";
    
    // JSP
    private static final String JSP_PAGE_PORTAL = "jsp/site/Portal.jsp";
    
    // Parameters
    private static final String PARAMETER_ID_TEST="id";
    private static final String PARAM_ACTION = "action";
    private static final String PARAM_PAGE = "page";
    
    // Markers
    private static final String MARK_TEST_LIST = "test_list";
    private static final String MARK_TEST = "test";
    
    // Message
    private static final String MESSAGE_CONFIRM_REMOVE_TEST = "providerappointment.message.confirmRemoveTest";
    
    // Views
    private static final String VIEW_MANAGE_TESTS = "manageTests";
    private static final String VIEW_CREATE_TEST = "createTest";
    private static final String VIEW_MODIFY_TEST = "modifyTest";

    // Actions
    private static final String ACTION_CREATE_TEST = "createTest";
    private static final String ACTION_MODIFY_TEST= "modifyTest";
    private static final String ACTION_REMOVE_TEST = "removeTest";
    private static final String ACTION_CONFIRM_REMOVE_TEST = "confirmRemoveTest";

    // Infos
    private static final String INFO_TEST_CREATED = "providerappointment.info.test.created";
    private static final String INFO_TEST_UPDATED = "providerappointment.info.test.updated";
    private static final String INFO_TEST_REMOVED = "providerappointment.info.test.removed";
    
    // Session variable to store working values
    private Test _test;
    
    @View( value = VIEW_MANAGE_TESTS, defaultView = true )
    public XPage getManageTests( HttpServletRequest request )
    {
        _test = null;
        Map<String, Object> model = getModel(  );
        model.put( MARK_TEST_LIST, TestHome.getTestsList(  ) );

        return getXPage( TEMPLATE_MANAGE_TESTS, request.getLocale(  ), model );
    }

    /**
     * Returns the form to create a test
     *
     * @param request The Http request
     * @return the html code of the test form
     */
    @View( VIEW_CREATE_TEST )
    public XPage getCreateTest( HttpServletRequest request )
    {
        _test = ( _test != null ) ? _test : new Test(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_TEST, _test );
           
        return getXPage( TEMPLATE_CREATE_TEST, request.getLocale(  ), model );
    }

    /**
     * Process the data capture form of a new test
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_TEST )
    public XPage doCreateTest( HttpServletRequest request )
    {
        populate( _test, request );

        // Check constraints
        if ( !validateBean( _test, getLocale( request ) ) )
        {
            return redirectView( request, VIEW_CREATE_TEST );
        }

        TestHome.create( _test );
        addInfo( INFO_TEST_CREATED, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_TESTS );
    }

    /**
     * Manages the removal form of a test whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_TEST )
    public XPage getConfirmRemoveTest( HttpServletRequest request ) throws SiteMessageException
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TEST ) );
        UrlItem url = new UrlItem( JSP_PAGE_PORTAL );
        url.addParameter( PARAM_PAGE, MARK_TEST );
        url.addParameter( PARAM_ACTION, ACTION_REMOVE_TEST );
        url.addParameter( PARAMETER_ID_TEST, nId );
        
        SiteMessageService.setMessage(request, MESSAGE_CONFIRM_REMOVE_TEST, SiteMessage.TYPE_CONFIRMATION, url.getUrl(  ));
        return null;
    }

    /**
     * Handles the removal form of a test
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage tests
     */
    @Action( ACTION_REMOVE_TEST )
    public XPage doRemoveTest( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TEST ) );
        TestHome.remove( nId );
        addInfo( INFO_TEST_REMOVED, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_TESTS );
    }

    /**
     * Returns the form to update info about a test
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_TEST )
    public XPage getModifyTest( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TEST ) );

        if ( _test == null  || ( _test.getId( ) != nId ))
        {
            _test = TestHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_TEST, _test );
        
        return getXPage( TEMPLATE_MODIFY_TEST, request.getLocale(  ), model );
    }

    /**
     * Process the change form of a test
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_TEST )
    public XPage doModifyTest( HttpServletRequest request )
    {
        populate( _test, request );

        // Check constraints
        if ( !validateBean( _test, getLocale( request ) ) )
        {
            return redirect( request, VIEW_MODIFY_TEST, PARAMETER_ID_TEST, _test.getId( ) );
        }

        TestHome.update( _test );
        addInfo( INFO_TEST_UPDATED, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_TESTS );
    }
}
