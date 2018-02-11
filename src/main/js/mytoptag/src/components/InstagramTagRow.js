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
import '../css/InstagramTagRow.css';

const INSTAGRAM_EXPLORE_URL='https://www.instagram.com/explore/tags/';

class InstagramTagRow extends Component {
  render() {
    const tagName = this.props.tag;
    const tagCount = this.props.count;
    return (
      <div className="component-instagram-tag-row">
        <div>
          <a href={INSTAGRAM_EXPLORE_URL+tagName} target="_blank">{tagName}</a>&nbsp;{tagCount}&nbsp;posts
        </div>
      </div>
    );
  }
}

InstagramTagRow.propTypes = {
  tag: PropTypes.string,
  count: PropTypes.number,
};

export default InstagramTagRow;