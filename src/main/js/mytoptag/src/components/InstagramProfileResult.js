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
import PropTypes from 'prop-types';
import InstagramTagRow from './InstagramTagRow.js';
import '../css/InstagramProfileResult.css';

const SORT_DIRECTION = {
    DESC: 'desc',
    ASC: 'asc'
}

const SORT_TYPE = {
    ALPHABETIC: 'tag',
    COUNT: 'count'
}

class InstagramProfileResult extends Component {
  constructor(props) {
    super(props);
    this.state = {sortDirection: SORT_DIRECTION.DESC, sortType: SORT_TYPE.COUNT};
    this.changeSortDirection = this.changeSortDirection.bind(this);
    this.changeSortType = this.changeSortType.bind(this);
  }

  changeSortType(event) {
    this.setState({sortType:
        this.state.sortType === SORT_TYPE.ALPHABETIC ? SORT_TYPE.COUNT : SORT_TYPE.ALPHABETIC});
  }

  changeSortDirection(event) {
    this.setState({sortDirection:
        this.state.sortDirection === SORT_DIRECTION.DESC ? SORT_DIRECTION.ASC : SORT_DIRECTION.DESC});
  }

  sortTags(tags, sortType, sortDirection) {
    return tags = tags.sort(function(a, b) {
        var direction = sortDirection === SORT_DIRECTION.ASC ? 1 : -1;
        var result;
        switch(sortType){
         case SORT_TYPE.ALPHABETIC:
            result = (a.tag < b.tag) ? -1 : 1;
            break;
         case SORT_TYPE.COUNT:
         default:
            result = a.count - b.count;
            break;
        }
        return result*direction;
    });
  }

  render() {
    var tags = this.sortTags(this.props.tags, this.state.sortType, this.state.sortDirection);
    return (
      <div className="component-instagram-profile-result">
      <div className="component-sort-buttons">
        Sort by
        <button className="sort-button-type" onClick={this.changeSortType}>{this.state.sortType}</button>
        <button className="sort-button-direction" onClick={this.changeSortDirection}>{this.state.sortDirection}</button>
      </div>
        {
          tags.map((tag) => {
            return (
              <InstagramTagRow className="component-instagram-tag-row"
                tag={tag.tag}
                count={tag.count}
              />
            );
          })
        }
      </div>
    );
  }
}

InstagramProfileResult.propTypes = {
  tags: PropTypes.array,
  sortType: PropTypes.string,
  sortDirection: PropTypes.string
};

export default InstagramProfileResult;