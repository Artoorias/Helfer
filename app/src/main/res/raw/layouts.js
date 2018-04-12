Vue.use(VueRouter);

// Ripple effect for buttons
Vue.use(VueTouchRipple, {
  color: '#666',
  opacity: 0.12
});

// Render answers as buttons
Vue.component('answer', {
  template: '#answer-template',
  props: ['answer', 'answers', 'next', 'idx'],
  mounted: function mounted() {
    var _this = this;

    var $el = this.$el;


    $el.on('mouseup', function (_) {
      if (_this.answers) {

        // Submit button
        var _iteratorNormalCompletion = true;
        var _didIteratorError = false;
        var _iteratorError = undefined;

        try {
          for (var _iterator = _this.answers[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {if (window.CP.shouldStopExecution(1)){break;}
            var answer = _step.value;

            if (answer.type === 'input') {
              // Android.set(this.idx + '-input-' + i, answer.value)
              if (!answer.value) return;
            }
            if (answer.type === 'checkbox' && answer.checked) {
              // Android.set(this.idx + '-input-' + i, answer.value)
            }
          }
window.CP.exitedLoop(1);

        } catch (err) {
          _didIteratorError = true;
          _iteratorError = err;
        } finally {
          try {
            if (!_iteratorNormalCompletion && _iterator.return) {
              _iterator.return();
            }
          } finally {
            if (_didIteratorError) {
              throw _iteratorError;
            }
          }
        }
      } else {

          // Normal button
          // Android.set(this.idx, this.answer);
        }

      setTimeout(function (_) {
        if (_this.next === false) {
          data.isSurveyFilled = true;
          return router.push('/');
        }

        router.push('/survey-' + _this.next);
      }, 250);
    });
  }
});

// Emojis!
emojione.imageType = 'svg';
Vue.component('icon', {
  props: ['emoji'],
  template: '<div class=\'icon\' v-html=\'getEmoji()\'></div>',
  methods: {
    getEmoji: function getEmoji() {
      return emojione.toImage(this.emoji);
    }
  }
});

// temp
if (typeof Android === 'undefined') {
  window.Android = {
    getData: function getData(_) {
      return {
        isSurveyFilled: true
      };
    },
    writeSurveyData: function writeSurveyData() {}
  };
}

var data = { isSurveyFilled: true };

// Root View
var RootView = {
  name: 'RootView',
  template: '#root-template',
  props: ['sections'],
  beforeRouteEnter: function beforeRouteEnter(to, from, next) {

    // request survey data
    if (!data.isSurveyFilled) {
      router.push('/survey-0');
    };

    next();
  }
};

// Survey View
var SurveyView = {
  name: 'SurveyView',
  template: '#survey-template',
  props: ['questions', 'idx'],
  methods: {
    getNext: function getNext() {
      if (+this.idx >= this.questions.length - 1) {
        return false;
      }

      return +this.idx + 1;
    }
  }
};

// Dish List View
var DishListView = {
  template: ''
};

// Router
var router = new VueRouter({
  routes: [{ path: '/', component: RootView, props: true }, { path: '/survey-:idx', component: SurveyView, props: true }, { path: '/dish-list', component: DishListView }]
});

Vue.component('slide-view', {
  template: '#slide-view-template',
  mounted: function mounted() {
    var _this2 = this;

    this.flickity = new Flickity(this.$el, {
      draggable: true,
      prevNextButtons: false,
      pageDots: false,
      initialIndex: 1,
      cellSelector: '.slide',
      cellAlign: 'left',
      selectedAttraction: 0.01,
      friction: 0.15
    });

    this.flickity.on('change', function (_) {
      _this2.ci = _this2.flickity.selectedIndex;
    });
  },
  data: function data() {
    return {
      ci: 1
    };
  }
});

Vue.component('profile-view', {
  template: '#profile-template',
  data: function data() {
    return {
      user: {
        sex: 0
      }
    };
  }
});

Vue.component('menu-view', {
  template: '#menu-template'
});

var articleData = JSON.parse(Android.getData(3));
var articles = articleData.success === true ? articleData.data : [];

// TODO: dodac grupe wiekowa

Vue.component('dashboard-view', {
  template: '#dashboard-template',
  data: function data() {
    return {
      articles: articles
    };
  }
});

// App
var app = new Vue({
  router: router,

  data: {
    sections: [{ button: { icon: '🔥', content: 'Survey', link: '/survey-0' } }, { button: { icon: '🍰', content: 'Food', link: '/survey-0' } }],
    questions: [{ prefix: 1, question: 'What is your gender?', icon: '👽', answers: ['Male', 'Female'] }, { prefix: 2, question: 'How old are you?', icon: '👵', answers: ['13-17', '18-25', '26-45', 'older than 65'] }, { prefix: 3, question: 'Your BMI', icon: '🐷', answers: [{ type: 'number', placeholder: 'Enter your weight (in kilograms)', value: '', methods: {
          onKeyup: function onKeyup(questions) {
            var span = q('.survey .question span');
            var i2 = q('.survey .iterator:nth-child(2) input');
            var bmi = ev.target.value / Math.pow(i2.value / 100, 2);
            if (bmi === Infinity || ('' + ev.target.value).length < 2 || ('' + i2).length < 3) bmi = 22;
            this.questions[this.idx].question = 'Your BMI: ' + bmi.toFixed(2);
            var icon = q('.survey header img');
            var scale = bmi / 22;
            icon.css('transform', 'scale(' + scale + ')');
          }
        } }, { type: 'number', placeholder: 'Enter your height (in centimeters)', value: '', methods: {
          onKeyup: function onKeyup(ev) {
            if (isNaN(+ev.key)) return false;
            var span = q('.survey .question span');
            var i1 = q('.survey .iterator:nth-child(1) input');
            var bmi = i1.value / Math.pow(ev.target.value / 100, 2);
            if (bmi === Infinity || ('' + ev.target.value).length < 3 || ('' + i1.value).length < 2) bmi = 22;
            this.questions[this.idx].question = 'Your BMI: ' + bmi.toFixed(2);
            var icon = q('.survey header img');
            var scale = bmi / 22;
            icon.css('transform', 'scale(' + scale + ')');
          }
        } }, { type: 'submit' }] }, { prefix: 4, question: 'What is your job?', icon: '🍟', answers: ['I’m a student', 'I do intelectual work', 'I do physical work', 'I am unemployed', 'I am retired'] }, { prefix: 5, question: 'How many meals a day do you usually eat?', icon: '🍩', answers: ['1', '2', '3', '4', '5 or more'] }, { prefix: 6, question: 'How much water do you drink every day?', icon: '☔', answers: ['Less than 1 litre', '1 litre', '2 litres', 'More than 2 litres'] }, { prefix: 7, question: 'How many hours of sleep do you get every day?', icon: '😪', answers: ['Less than 6 hours', '6-8 hours', 'More than 8 hours'] }, { prefix: 8, question: 'What are your bad habits? (you can choose more than one)', icon: '😈', answers: [{ type: 'checkbox', value: 'Smoking', checked: false }, { type: 'checkbox', value: 'Drinking alcohol', checked: false }, { type: 'checkbox', value: 'Wasting time on computer/TV/phone', checked: false }, { type: 'checkbox', value: 'Eating unhealthy meals', checked: false }, { type: 'submit' }] }, { prefix: 9, question: 'Allergies', icon: '😿', answers: [{ type: 'submit' }] }, { prefix: 10, question: 'Physical illnesses', icon: '🏀', answers: [{ type: 'submit' }] }, { prefix: 11, question: 'Chronic diseases', icon: '😷', answers: [{ type: 'submit' }] }, { prefix: 12, question: 'How much time can you spend on exercising every day?', icon: '🕰', answers: ['Less than 15 minutes', '15-25 minutes', '20-60 minutes', 'More than an hour'] }, { prefix: 13, question: 'Do you have a special diet?', icon: '🌷', answers: [{ type: 'checkbox', value: 'I’m a vegetarian', checked: false }, { type: 'checkbox', value: 'I’m a vegan', checked: false }, { type: 'submit' }] }]
  }
}).$mount('#app');