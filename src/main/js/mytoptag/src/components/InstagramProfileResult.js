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
import InstagramPostRow from  './InstagramPostRow.js';
import '../css/InstagramProfileResult.css';

const SORT_DIRECTION = {
    DESC: 'desc',
    ASC: 'asc'
}

const TAG_SORT_TYPE = {
    ALPHABETIC: 'tag',
    COUNT: 'count'
}

const POST_SORT_TYPE = {
    DATE: 'date',
    LIKES: 'likes'
}

const VIEW_MODE = {
    TAGS: 'Tags',
    POSTS: 'Posts'
}

class InstagramProfileResult extends Component {
  constructor(props) {
    super(props);
    this.state = {
        posts: this.props.posts,
        tags: this.props.tags,
        tagSortDirection: SORT_DIRECTION.DESC,
        tagSortType: TAG_SORT_TYPE.COUNT,
        postSortDirection: SORT_DIRECTION.DESC,
        postSortType: POST_SORT_TYPE.DATE,
        viewMode: VIEW_MODE.POSTS
    };
    this.changeTagSortDirection = this.changeTagSortDirection.bind(this);
    this.changeTagSortType = this.changeTagSortType.bind(this);
    this.changePostSortDirection = this.changePostSortDirection.bind(this);
    this.changePostSortType = this.changePostSortType.bind(this);
    this.showPosts = this.showPosts.bind(this);
    this.showTags = this.showTags.bind(this);
    this.renderSortButtons = this.renderSortButtons.bind(this);
    this.renderContent = this.renderContent.bind(this);
  }

  /*
    Tags
  */
  changeTagSortType(event) {
    this.setState({tagSortType:
        this.state.tagSortType === TAG_SORT_TYPE.ALPHABETIC ? TAG_SORT_TYPE.COUNT : TAG_SORT_TYPE.ALPHABETIC});
  }

  changeTagSortDirection(event) {
    this.setState({tagSortDirection:
          this.state.tagSortDirection === SORT_DIRECTION.DESC ? SORT_DIRECTION.ASC : SORT_DIRECTION.DESC});
  }

  showTags(event) {
    this.setState({viewMode: VIEW_MODE.TAGS});
  }

  /*
    Posts
  */
  changePostSortType(event) {
    this.setState({postSortType:
          this.state.postSortType === POST_SORT_TYPE.DATE ? POST_SORT_TYPE.LIKES : POST_SORT_TYPE.DATE});
  }

  changePostSortDirection(event) {
    this.setState({postSortDirection:
        this.state.postSortDirection === SORT_DIRECTION.DESC ? SORT_DIRECTION.ASC : SORT_DIRECTION.DESC});
  }

  showPosts(event) {
    this.setState({viewMode: VIEW_MODE.POSTS});
  }

  sortTags(tags, sortType, sortDirection) {
    return tags = tags.sort(function(a, b) {
        var direction = sortDirection === SORT_DIRECTION.ASC ? 1 : -1;
        var result;
        switch(sortType){
         case TAG_SORT_TYPE.ALPHABETIC:
            result = (a.tag < b.tag) ? -1 : 1;
            break;
         case TAG_SORT_TYPE.COUNT:
         default:
            result = a.count - b.count;
            break;
        }
        return result*direction;
    });
  }

  sortPosts(posts, sortType, sortDirection) {
    return posts = posts.sort(function(a, b) {
        var direction = sortDirection === SORT_DIRECTION.ASC ? 1 : -1;
        var result;
        switch(sortType){
         case POST_SORT_TYPE.DATE:
            result = a.id - b.id;
            break;
         case POST_SORT_TYPE.LIKES:
         default:
            result = a.likes - b.likes;
            break;
        }
        return result*direction;
    });
  }


  renderSortButtons(viewMode) {
    return <div className="component-sort-buttons">
              Sort by
              <button className="sort-button-type"
                onClick={
                    viewMode === VIEW_MODE.TAGS ? this.changeTagSortType : this.changePostSortType
                }>
                {viewMode === VIEW_MODE.TAGS ? this.state.tagSortType : this.state.postSortType}
              </button>
              <button className="sort-button-direction"
                onClick={
                    viewMode === VIEW_MODE.TAGS ? this.changeTagSortDirection : this.changePostSortDirection
                }>
               {viewMode === VIEW_MODE.TAGS ? this.state.tagSortDirection : this.state.postSortDirection}
              </button>
          </div>
  }

  renderContent(viewMode) {
    var content;
    if (viewMode === VIEW_MODE.TAGS) {
      var tags = this.sortTags(this.props.tags, this.state.tagSortType, this.state.tagSortDirection);
      content = tags.map((tag) => {
          return (
            <InstagramTagRow className="component-instagram-tag-row"
              tag={tag.tag}
              count={tag.count}
            />
          );
        });
    } else {
      var posts = this.sortPosts(this.props.posts, this.state.postSortType, this.state.postSortDirection);
      content = posts.map((post) => {
          return (
            <InstagramPostRow className="component-instagram-post-row"
              shortCode={post.shortCode}
              previewLink={post.previewLink}
              tags={post.tags}
              likes={post.likes}
            />
          );
        });
    }
    return content;
  }

  render() {
    var sortButtons = this.renderSortButtons(this.state.viewMode);
    var content = this.renderContent(this.state.viewMode);
    return (
      <div className="component-instagram-profile-result">
      <div className="component-switch-show">
        <button className="switch-show-posts" onClick={this.showPosts}>Posts</button>
        <button className="switch-show-tags" onClick={this.showTags}>Tags</button>
      </div>
        {sortButtons}
        {content}
      </div>
    );
  }
}

InstagramProfileResult.propTypes = {
  posts: PropTypes.array,
  tags: PropTypes.array,
  viewMode: PropTypes.string,
  tagSortType: PropTypes.string,
  tagSortDirection: PropTypes.string,
  postSortType: PropTypes.string,
  postSortDirection: PropTypes.string
};

export default InstagramProfileResult;