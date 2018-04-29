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
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import ReactTooltip from 'react-tooltip'
import config from '../app-config.js';

class InstagramPostTag extends Component {

  constructor(props) {
    super(props);
    this.state = {
          tag: this.props.tag,
          count: null
        };
    this.loadStats = this.loadStats.bind(this);
  }

  loadStats() {
    if (this.state.count === null) {
      var request = config.api_url + `tag/${encodeURIComponent(this.state.tag)}`;
      fetch(request).then(function(response) {
                return response.json();
              }).then((json) => {
                const resultCount = json.lastHistoryEntry.count
                this.setState({count: resultCount});
            })
    }
    return this.state.count;
  }

  render() {
    const tag = this.props.tag;
    return (
      <span>
        <a data-tip data-for={'tagStats_' + tag} data-event='click focus'>
            <span><a href={"#" + tag}>{"#" + tag}</a> </span>
        </a>
        <ReactTooltip id={'tagStats_'+ tag} type='dark' place="left" effect="solid">
          <span>Posts with this tag:{this.loadStats()}</span>
        </ReactTooltip>
      </span>
    );
  }

}

InstagramPostTag.propTypes = {
  tag: PropTypes.string,
  count: PropTypes.number
};

export default InstagramPostTag;