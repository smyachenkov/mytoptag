/*
 * Copyright (c) 2018 Stanislav Myachenkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
import React, {Component} from 'react';
import InstagramProfileResult from "./InstagramProfileResult.js";
import '../css/ProfileNameInput.css';
import config from '../app-config.js';

class ProfileNameInput extends Component {
  constructor(props) {
    super(props);
    this.state = {profilename: '', tags: []};
    this.handleChange = this.handleChange.bind(this);
    this.searchProfile = this.searchProfile.bind(this);
    this.handleKeyPress = this.handleKeyPress.bind(this);
  }

  handleChange(event) {
    this.setState({profilename: event.target.value});
  }

  handleKeyPress(event) {
    if (event.key === 'Enter') {
      this.searchProfile(event);
    }
  }

  searchProfile(event)  {
    var url = config.api_url + `profile/tags/${encodeURIComponent(this.state.profilename)}/counted=true`;
    fetch(url).then(function(response) {
        return response.json();
      }).then((json) => {
        if(json.data) {
          var resultTags = []
          json.data.forEach(function(item, i, arr) {
              resultTags.push(item);
          })
          this.setState({tags: resultTags});
        }
    })
  }

  render() {
    return (
      <div>
        <label>
          <input className="search-input" onKeyPress={this.handleKeyPress} value={this.state.profilename} onChange={this.handleChange} />
        </label>
        <button className="search-button" onClick={this.searchProfile}>Search</button>
        <InstagramProfileResult tags={this.state.tags}
        />
      </div>
    );
  }
}

export default ProfileNameInput;