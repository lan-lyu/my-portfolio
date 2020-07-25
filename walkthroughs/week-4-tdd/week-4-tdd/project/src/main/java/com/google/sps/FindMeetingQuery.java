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

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import com.google.sps.TimeRange;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //throw new UnsupportedOperationException("TODO: Implement this method.");

    Collection<String> requestAttendees = request.getAttendees();
    ArrayList<TimeRange> timeSegList = new ArrayList<TimeRange>();
    ArrayList<TimeRange> res = new ArrayList<TimeRange>();
        
    if (requestAttendees.isEmpty()) {
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    
    timeSegList=getTimeRanges(events,requestAttendees);
    timeSegList.sort(TimeRange.ORDER_BY_START);

    int last = 0;
    for (TimeRange tmp: timeSegList) {
      if(tmp.end() <= last) continue;
      if(tmp.start()<last) continue;
      if (tmp.start() - last >= request.getDuration()) {
        res.add(TimeRange.fromStartEnd(last, tmp.start(), false));
      }
      last = tmp.end();
    }
    if (TimeRange.END_OF_DAY - last + 1 >= request.getDuration()) {
      res.add(TimeRange.fromStartEnd(last, TimeRange.END_OF_DAY, true));
    }

    return res;
  }

    private ArrayList<TimeRange> getTimeRanges(
    Collection<Event> events, 
    Collection<String> attendees) {

        ArrayList<TimeRange> Ranges = new ArrayList<TimeRange>();

        for (Event nowEvent : events) {
            Set<String> eventAttendees = nowEvent.getAttendees();
            int flag = 0;

            for (String nowAtt : attendees) {
                if (eventAttendees.contains(nowAtt)) {
                flag = 1;
                break;
                }
            }

            if (flag==0) {continue;}
            Ranges.add(nowEvent.getWhen());
        }
        
        return Ranges;
    }
}