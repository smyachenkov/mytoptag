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

class InstagramPostTag extends Component {

  constructor(props) {
    super(props);
    this.state = {
          tag: this.props.tag,
          count: this.props.count
        };
  }

  render() {
    const tag = this.state.tag;
    const tagToTooltipLength = 95+tag.length*4;
    return (
      <span className="post-tag-entry">
        <a data-tip data-for={'tag-stats-' + tag} data-event='click focus'>
            <span>
              <a>
                {"#" + tag}
              </a>
            </span>
        </a>
        <ReactTooltip
            id={'tag-stats-'+ tag}
            type='info'
            place="right"
            effect="solid"
            offset={{right: tagToTooltipLength, bottom: 9}}
            getContent={[() => {
              return "Posts with this tag:" + this.state.count
              }]
            }>
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