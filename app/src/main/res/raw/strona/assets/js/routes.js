const _views = [
  [ 'about', 'About the project' ],
  [ 'profile', 'My profile' ],
  [ 'sport', 'Sport', [
    { title: 'Upper body parts', template: 'sport-sub', path: 'sport-upper', children: [
      { title: 'Lower body parts', template: 'sport-sub', path: 'sport-lower' },
    ] },
    { title: 'Lower body parts', template: 'sport-sub', path: 'sport-lower' },
    { title: 'Back', template: 'sport-sub', path: 'sport-back' },
  ] ],
  [ 'food', 'Food', [
    { title: 'Vegan', template: 'food-sub', path: 'food-vega' },
    { title: 'Vegetarian', template: 'food-sub', path: 'food-vege' },
    { title: 'Gluten free', template: 'food-sub', path: 'food-gluten' },
  ] ],
  [ 'health', 'Health' ],
  [ 'like', 'You may like it', [
    { title: 'Videos', template: 'like-sub-videos', path: 'like-videos' },
    { title: 'Articles', template: 'like-sub-articles', path: 'like-articles' },
  ] ],
  [ 'field', 'Field game' ],
]

const views = []

const process = V => {
  if (Array.isArray(V)) {
    const things = (V[2]||[]).map(process)
    
    const route = [ V[0], V[0], {
      data () {
        return { title: V[1], things }
      }
    } ]
    views.push(route)
    return route
  }
  
  const route = [ V.path, V.template, { data () { return { title: V.title } } }, (V.children||[]).map(process) ]
  views.push(route)
  return route
}

_views.map(process)

const routes =  [
  [ '/', 'home', {
    data () {
      return {
        motd: 'Motto of the day',
        views: _views,
      }
    }
  } ],
  ...views
]

export default routes.map(R => {
  let path = R
  let tmpl = R
  let opts = {}
  
  if (Array.isArray(R)) {
    path = R[0][0] === '/' ? R[0].slice(1) : R[0]
    tmpl = typeof R[1] === 'string' ? R[1] : path
    opts = !!R[2] ? R[2] : opts
  }
  
  return {
    path: '/' + path,
    component: {
      template: '#' + tmpl,
      ...opts,
    }
  }
})