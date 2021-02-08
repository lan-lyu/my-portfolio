// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/comments")
public class DataServlet extends HttpServlet {

  private ArrayList<String> ls;


  @Override
  public void init(){
      ls = new ArrayList<>();
      /*
      ls.add("Here is comment 1.");
      ls.add("Here is comment 2.");
      ls.add("Here is comment 3.");
      */
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Task").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<String> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String title = (String) entity.getProperty("title");
      long timestamp = (long) entity.getProperty("timestamp");
      String comment = title;

      comments.add(comment);
    }

    Gson gson = new Gson();

    response.setContentType("application/json; charset=UTF-8");
    response.getWriter().println(gson.toJson(comments));
  }

/*
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String templs = ls.get((int) (Math.random() * ls.size()));
    
    //convert to json
    String json = "{";
    json += templs;
    json += "}";

    response.setContentType("text/html;");
    response.getWriter().println(templs);
  }
*/

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // Get the input from the form.
    String ttext = getParameter(request, "comment", "");
    long timestamp = System.currentTimeMillis();

    ls.add(ttext);
    
    Entity taskEntity = new Entity("Task");
    taskEntity.setProperty("title", ttext);
    taskEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);

    response.sendRedirect("/photo.html");
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}

