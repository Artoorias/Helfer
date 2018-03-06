Vue.use(VueRouter);

// Ripple effect for buttons
Vue.component('paper-button', {
  template: `#button-template`,
  props: ['title'],
  mounted () {
    const { $el } = this;
    
    const ripple = new PaperRipple();

    $el.append(ripple.$);
    
    $el.on('touchstart', ev => ripple.downAction(ev));
    $el.on('touchend', ev => ripple.upAction());
    $el.on('mousedown', ev => ripple.downAction(ev));
    $el.on('mouseup', ev => ripple.upAction());
  },
})

// Render answers as buttons
Vue.component('answer', {
  template: `#answer-template`,
  props: [ 'answer', 'answers', 'next', 'idx' ],
  mounted () {
    const { $el } = this;
    
    $el.on('mouseup', _ => {
      if (this.answers) {
        
        // Submit button
        for (const answer of this.answers) {
          if (answer.type === 'input') {
            // Android.set(this.idx + '-input-' + i, answer.value)
            if (!answer.value) return;
          }
          if (answer.type === 'checkbox' && answer.checked) {
            // Android.set(this.idx + '-input-' + i, answer.value)
          }
        }
      } else {
        
        // Normal button
        // Android.set(this.idx, this.answer);
      }
      
      
      setTimeout(_ => {
        if (this.next === false) {
          data.isSurveyFilled = true;
          return router.push('/');
        }
        
        router.push('/survey-' + this.next);
      }, 250)
    })
  }
})


// Emojis!
emojione.imageType = 'svg';
Vue.component('icon', {
  props: [ 'emoji' ],
  template: `<div class='icon' v-html='getEmoji()'></div>`,
  methods: {
    getEmoji () {
      return emojione.toImage(this.emoji);
    }
  }
})

// temp
const Android = { getData: _ => ({
  isSurveyFilled: true,
}) };

const data = Android.getData();

// Root View
const RootView = {
  name: 'RootView',
  template: `#root-template`,
  props: [ 'sections' ],
  beforeRouteEnter (to, from, next) {
    
    // request survey data
    if (!data.isSurveyFilled) {
      router.push('/survey-0');
    };
    
    next()
  },
};

// Survey View
const SurveyView = {
  name: 'SurveyView',
  template: `#survey-template`,
  props: [ 'questions', 'idx' ],
  methods: {
    getNext() {
      if (+this.idx >= this.questions.length - 1) {
        return false;
      }
      
      return +this.idx + 1;
    }
  }
};

// Dish List View
const DishListView = {
  template: ``,
};

// Router
const router = new VueRouter({
  routes: [
    { path: '/', component: RootView, props: true },
    { path: '/survey-:idx', component: SurveyView, props: true },
    { path: '/dish-list', component: DishListView },
  ],
})

// App
const app = new Vue({
  router,
  
  data: {
    sections: [
      { button: { icon: '🔥', content: 'Survey', link: '/survey-0' } },
      { button: { icon: '🍰', content: 'Food', link: '/survey-0' } },
    ],
    questions: [
      { prefix: 1, question: 'What is your gender?', icon: '👽', answers: [ 'Male', 'Female' ] },
      { prefix: 2, question: 'How old are you?', icon: '👵', answers: [ '13-17', '18-25', '26-45', 'older than 65' ] },
      { prefix: 3, question: 'Your BMI', icon: '🐷', answers: [
        { type: 'input', placeholder: 'Enter your weight (in kilograms)', value: '' },
        { type: 'input', placeholder: 'Enter your height (in centimeters)', value: '' },
        { type: 'submit' }
      ] },
      { prefix: 4, question: 'What is your job?', icon: '🍟', answers: [ 'I’m a student', 'I do intelectual work', 'I do physical work', 'I am unemployed', 'I am retired' ] },
      { prefix: 5, question: 'How many meals a day do you usually eat?', icon: '🍩', answers: [ '1', '2', '3', '4', '5 or more' ] },
      { prefix: 6, question: 'How much water do you drink every day?', icon: '☔', answers: [ 'Less than 1 litre', '1 litre', '2 litres', 'More than 2 litres' ] },
      { prefix: 7, question: 'How many hours of sleep do you get every day?', icon: '😪', answers: [ 'Less than 6 hours', '6-8 hours', 'More than 8 hours' ] },
      { prefix: 8, question: 'What are your bad habits? (you can choose more than one)', icon: '😈', answers: [
        { type: 'checkbox', value: 'Smoking', checked: false },
        { type: 'checkbox', value: 'Drinking alcohol', checked: false },
        { type: 'checkbox', value: 'Wasting time on computer/TV/phone', checked: false },
        { type: 'checkbox', value: 'Eating unhealthy meals', checked: false },
        { type: 'submit' }
      ] },
      { prefix: 9, question: 'Allergies', icon: '😿', answers: [
        { type: 'submit' }
      ] },
      { prefix: 10, question: 'Physical illnesses', icon: '🏀', answers: [
        { type: 'submit' }
      ] },
      { prefix: 11, question: 'Chronic diseases', icon: '😷', answers: [
        { type: 'submit' }
      ] },
      { prefix: 12, question: 'How much time can you spend on exercising every day?', icon: '🕰', answers: [ 'Less than 15 minutes', '15-25 minutes', '20-60 minutes', 'More than an hour' ] },
      { prefix: 13, question: 'Do you have a special diet?', icon: '🌷', answers: [
        { type: 'checkbox', value: 'I’m a vegetarian', checked: false },
        { type: 'checkbox', value: 'I’m a vegan', checked: false },
        { type: 'submit' }
      ] },
    ],
  }
}).$mount('#app');