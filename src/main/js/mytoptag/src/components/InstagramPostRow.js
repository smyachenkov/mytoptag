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
import '../css/InstagramPostRow.css';
import InstagramPostTag from './InstagramPostTag.js';

const INSTAGRAM_POST_URL='https://www.instagram.com/p/';

class InstagramPostRow extends Component {

  constructor(props) {
    super(props);
    this.state = {
          shortCode: this.props.shortCode,
          previewLink: this.props.previewLink,
          tags: this.props.tags,
          likes: this.props.likes
        };
  }

  render() {
    const postLink = INSTAGRAM_POST_URL + this.props.shortCode;
    return (
      <div className="component-instagram-post-row">
        <div className="component-instagram-post-likes">
          ❤  {this.props.likes}
        </div>
        <div className="component-instagram-post-preview">
          <a href={postLink} target="_blank">
            <img src={this.props.previewLink} width="100" height="100" alt="view"/>
          </a>
        </div>
        <div className="component-instagram-post-tags">
          { this.props.tags.map(t =>
            { return <InstagramPostTag className="component-instagram-post-tag" tag={t.tag} count={t.count}/> })
          }
        </div>
      </div>
    );
  }
}

InstagramPostRow.propTypes = {
  shortCode: PropTypes.string,
  previewLink: PropTypes.string,
  tags: PropTypes.array,
  likes: PropTypes.number
};


export default InstagramPostRow;