test = {
  'name': 'Question 2',
  'points': 1,
  'suites': [
    {
      'cases': [
        {
          'code': r"""
          >>> free_bacon(35)
          4
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> free_bacon(71)
          2
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> free_bacon(7)
          1
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> free_bacon(17)
          2
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> free_bacon(0)
          1
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> free_bacon(10)
          1
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> free_bacon(70)
          1
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> free_bacon(99)
          10
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> free_bacon(57)
          6
          """,
          'hidden': False,
          'locked': False
        }
      ],
      'scored': True,
      'setup': r"""
      >>> from hog import *
      """,
      'teardown': '',
      'type': 'doctest'
    }
  ]
}
