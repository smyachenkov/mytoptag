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
import '../css/SearchInput.css';
import config from '../app-config.js';

const SEARCH_TYPE = {
    TAGS: 'Tags',
    PROFILE: 'Profile'
}

// todo accept tags as space or comma separated list
class SearchInput extends Component {
  constructor(props) {
    super(props);
    this.state = {
      input: '',
      searchType: SEARCH_TYPE.PROFILE,
      tags: [],
      posts: []
    };
    this.handleChange = this.handleChange.bind(this);
    this.search = this.search.bind(this);
    this.handleKeyPress = this.handleKeyPress.bind(this);
  }

  handleChange(event) {
    this.setState({input: event.target.value});
  }

  handleKeyPress(event) {
    if (event.key === 'Enter') {
      this.search(event);
    }
  }

  search(event) {
    this.setState({tags:[], posts: []})
    if (this.state.input.startsWith('#')) {
      // todo implement tag search
    } else {
      var tags_url = config.api_url + `profile/tags/${encodeURIComponent(this.state.input)}/counted=true`;
      fetch(tags_url).then(function(response) {
          return response.json();
        }).then((json) => {
          if(json.data) {
            this.setState({tags: json.data});
          }
      })
      var posts_url = config.api_url + `profile/posts/${encodeURIComponent(this.state.input)}/counted=true`;
      fetch(posts_url).then(function(response) {
          return response.json();
        }).then((json) => {
          if(json.data) {
            this.setState({posts: json.data});
          }
      })
    }
  }

  render() {
    return (
      <div>
        <label>
          <input className="search-input"
            onKeyPress={this.handleKeyPress}
            value={this.state.input}
            onChange={this.handleChange}
          />
        </label>
        <button className="search-button"
            onClick={this.search}>Search
        </button>
        <InstagramProfileResult
            tags={this.state.tags}
            posts={this.state.posts}
        />
      </div>
    );
  }
}

export default SearchInput;