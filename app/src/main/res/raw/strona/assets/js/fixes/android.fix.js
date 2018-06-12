if (typeof Android === 'undefined') {
  window.Android = {
    getData () { return '{ "success": false }' },
    getResources () { return '{ "success": false }' },
    getSurveyInfoAll () { return '{ "success": true, "data": [{}] }' },
    writeSurveyData () {},
  };
}