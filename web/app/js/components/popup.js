'use strict';

var React = require('react');
var sc = require('spatialconnect');
var FeatureDetails = require('./featuredetails');
var Modal = require('react-modal');

var Popup = React.createClass({
  getInitialState: function() {
    return {
      selectedFeature: null,
      modalIsOpen: false
    };
  },
  componentDidMount: function() {
    var that = this;
    var map = this.props.map;
    var element = document.getElementById('popup');
    var popup = new ol.Overlay({
      element: element,
      positioning: 'bottom-center',
      stopEvent: false
    });
    map.addOverlay(popup);
    // display popup on click
    map.on('click', function(evt) {
      var feature = map.forEachFeatureAtPixel(evt.pixel,
          function(feature, layer) {
            return feature;
          });
      if (feature) {
        that.setState({
          selectedFeature: feature
        });
        popup.setPosition(evt.coordinate);
        $(element).popover({
          'placement': 'top',
          'html': true,
          'content': document.getElementById('popup-content')
        });
        $(element).popover('show');
      } else {
        $(element).popover('destroy');
      }
    });
  },
  showDetails: function() {
    this.setState({
      modalIsOpen: true
    });
  },
  render: function() {
    return (
      <div>
        <Modal isOpen={this.state.modalIsOpen}>
          <FeatureDetails feature={this.state.selectedFeature} />
        </Modal>
        <div id="popup" className="ol-popup">
          <a href="#" id="popup-closer" className="ol-popup-closer"></a>
          <div id="popup-content" onClick={this.showDetails}>
            <span>
              Feature Id: {this.state.selectedFeature ? this.state.selectedFeature.get('id') : 'None'}
            </span>
          </div>
        </div>
      </div>
    );
  }
});

module.exports = Popup;
